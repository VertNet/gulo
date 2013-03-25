(ns gulo.ipt
  "Functions for harvesting Darwin Core Archives from IPT.

  A Darwin Core Archive (archive) is a zip file that contains a Darwin
  Core record dataset with an EML metadata document. The metadata
  describes what the dataset is and when it was last updated.

  Archives are published online by the Integrated Publishing
  Toolkit (IPT) as resources. Each resource has an RSS feed that
  describes the resource, when it was last published, and provides
  download links to the archive.

  For convienience, we store resource URLs that we want to harvest in
  CartoDB:

    https://vertnet.cartodb.com/tables/resource

  The resource table has the resource_pubdate for when the resource
  was last published, the dataset_pubdate for when the dataset was
  last updated, and the link to the resource.

  When we harvest resources, we extract the dataset and metadata from
  their archive, lookup the resource organization if possible, and
  encode everything (records, resource, dataset, organization) into a
  graph-based schema using Thrift. The schema definition is in
  dev/gulo.thrift. The basic nodes in our schema are Organization,
  Resource, Dataset, and Record:
 
    +----------+    +----------+    +----------+    +--------------+
    |          |    |          |    |          |    |              |
    | Record   +--->+ Dataset  +--->+ Resource +--->+ Organization |
    |          |    |          |    |          |    |              |
    +----------+    +----------+    +----------+    +--------------+
 
  We then store the graph in S3 using Pails so that we can easily
  process them into views using MapReduce on Hadoop via Cascading and
  Cascalog. The resulting views are ultimately uploaded to Google App
  Engine and CartoDB for serving core VertNet APIs and UIs.

  The harvesting workflow looks like this:

    (1) Grab resources from our CartoDB resource table

    (2) For each resource, compare resource_pubdate with the RSS feed pubdate

    (3) If resource_pubdate equals RSS pubdate, skip resource

    (4) If resource_pubdate older than RSS feed pubdate, then:
        - Download resource RSS
        - If RSS contains GUID, download resource organiation metadata
        - Download and extract dataset and metadata from resource archive
        - Encode resource, dataset, organization, records into graph
        - upload graph to S3
        - Update resource_pubdate in CartoDB resource table
  
  For reference:

    Darwin Core: http://goo.gl/HgvY4
    Darwin Core Archive: http://goo.gl/ee3KC
    EML: http://goo.gl/Z27H5
    IPT: http://goo.gl/GtJMF
    Thrift: http://goo.gl/S5xmY
    S3: http://goo.gl/ailE
    Pails: http://goo.gl/MjVkn
    Hadoop: http://goo.gl/tnkf
    MapReduce: http://goo.gl/Dmj3
    Cascading: http://goo.gl/9PaFv
    Cascalog: http://goo.gl/SRmDh
  "
  (:use [clojure.java.io :as io]
        [cascalog.api]
        [net.cgrand.enlive-html :as html
         :only (html-resource, select, attr-values)]
        [clojure.data.json :only (read-json)]
        [cartodb.core :as cartodb]
        [cartodb.utils :as cartodb-utils]
        [gulo.thrift :as t]
        [gulo.hadoop.pail :as p]
        [dwca.core :as dwca]
        [gulo.views :as views])
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.contrib.zip-filter.xml :as z])
  (:import [java.io File ByteArrayInputStream]
           [org.gbif.metadata.eml EmlFactory]
           [org.json XML]
           [gulo.schema DatasetRecordEdge ResourceDatasetEdge ResourceOrganizationEdge]))

;; Slurps resources/creds.json for CartoDB creds:
(def cartodb-creds (read-json (slurp (io/resource "creds.json"))))

;; CartoDB API Key:
(def api-key (:api_key cartodb-creds))

;; RSS feed URL for the VertNet IPT instance:
(def vertnet-ipt-rss "http://ipt.vertnet.org:8080/ipt/rss.do")

(defn sink-metadata
  "Sinks metadata for supplied resource, dataset, and organization to a Pail.

  Args:
    pail: String path to root pail directory.
    resource: {:creator :author :eml :guid :title :orgurl :pubDate :publisher
               :description :dwcaUrl}                            
    dataset: {:title :creator :metadataProvider :language :associatedParty
              :pubDate :contact :additionalInfo :guid}
    organization: {:primaryContactType :nodeName :primaryContactDescription :key
                   :name :primaryContactAddress :primaryContactName :nameLanguage
                   :primaryContactPhone :homepageURL :descriptionLanguage
                   :description :nodeKey :nodeContactEmail :primaryContactEmail}"
  [pail resource dataset organization]
  (let [resource-data (t/resource-data resource)
        resource-id (views/get-ResourceProperty-id (ffirst resource-data))
        dataset-data (t/dataset-data dataset)
        dataset-id (views/get-DatasetProperty-id (ffirst dataset-data))
        organization-data (t/organization-data organization)
        organization-id (views/get-OrganizationProperty-id organization-data)
        resource-dataset-edge (ResourceDatasetEdge.
                               (t/create-resource-id resource-id)
                               (t/create-dataset-id dataset-id))
        resource-organization-edge (ResourceOrganizationEdge.
                                    (t/create-resource-id resource-id)
                                    (t/create-organization-id organization-id))]
    (p/to-pail pail resource-data)
    (p/to-pail pail dataset-data)
    (p/to-pail pail organization-data)
    (p/to-pail pail [[resource-dataset-edge]])
    (p/to-pail pail [[resource-organization-edge]])
    dataset-id))

(defn mk-DatasetRecordEdge
  [d dataset-id]
  (->> (views/get-RecordProperty-id d)
       (t/create-rec-id)
       (DatasetRecordEdge. (t/create-dataset-id dataset-id))
       (t/edge-data)))

(defn sink-data
  "Sink all resource records to supplied pail."
  [pail resource dataset-id]
  (let [dataset-guid (:guid resource)
        dwca-url (:dwca resource)
        dwc-records (dwca/open dwca-url)
        records (map fields dwc-records)
        record-data (map (partial t/record-data dataset-guid) records)]
    (doall
     (for [d record-data]
       (do 
         (p/to-pail pail [[(mk-DatasetRecordEdge d dataset-id)]])
         (p/to-pail pail (<- [?d] (d ?d))))))))

(defprotocol IResourceTable
  "Protocol for working with a CartoDB response with all resource table rows."
  (dwca-urls [this] "Return sequence of archive URLs.")
  (resource-urls [this] "Return sequence of resource URLs")
  (pubdate [this url] "Return pubdate for supplied resource URL.")
  (update-pubdate [this url pubdate] "Update resource_pubdate.")
  (sync-new [this table tmp] "Return new resources since last sync.")
  (sync-updated [this table tmp] "Return updated resources since last sync.")
  (sync-deleted [this table tmp] "Return deleted resources since last sync"))

(defrecord ResourceTable   
  [results] ;; CartoDB result JSON for 'SELECT * FROM resource'
  IResourceTable
  (dwca-urls
    [_]
    (map :dwca (:rows results)))
  (resource-urls
    [_]
    (map :link (:rows results)))  
  (pubdate
    [_ url]
    (when-let [resource (first (filter #(= (:url %) url) (:rows results)))]
      (:resource_pubdate resource)))
  (update-pubdate
    [_ url pubdate]
    (when-let [resource (first (filter #(= (:url %) url) (:rows results)))]
      (cartodb/query
       (format "UPDATE resource SET resource_pubdate = '%s' WHERE url = '%s'"
               (:resource_pubdate resource)
               url)
       "vertnet")))
  (sync-new
    [_ table tmp]
    (let [template "SELECT %s.* FROM %s LEFT OUTER JOIN %s USING (datasetguid) WHERE %s.datasetguid is null" 
          sql (format template tmp tmp table table)]
      (:rows (cartodb/query sql "vertnet" :api-key api-key))))
  (sync-updated
    [_ table tmp]
    (let [template "SELECT %s.* FROM %s, %s WHERE %s.datasetguid = %s.datasetguid AND %s.pubdate <> %s.pubdate"
          sql (format template tmp tmp table tmp table tmp table)]
      (:rows (cartodb/query sql "vertnet" :api-key api-key))))
  (sync-deleted
    [_ table tmp]
    (let [template "SELECT * FROM %s LEFT OUTER JOIN %s USING (datasetguid) WHERE %s.datasetguid is null"
          sql (format template table tmp tmp)
          rows (:rows (cartodb/query sql "vertnet" :api-key api-key))]
      (filter #(not= nil (:cartodb_id %)) rows))))

(defn resource-table-response
  "Return CartoDB response JSON for all resource table records."
  []
  (cartodb/query "SELECT * FROM resource" "vertnet" :api-version "v1" :api-key api-key))

;; The resource table.
(def resource-table (ResourceTable. (resource-table-response)))

(defn eml->dataset
  "Return DatasetPropertyValue map from supplied org.gbif.metadata.eml.Eml obj."
  [eml]  
  {:title (.getTitle eml)
   :creator (.getCreatorEmail eml)
   :metadataProvider (.getEmail (.getMetadataProvider eml))
   :language (.getLanguage eml)
   :associatedParty (reduce #(str %1 "," %2)
                            (for [x (.getAssociatedParties eml)] (.getEmail x)))
   :pubDate (.toString (.getPubDate eml))
   :contact (.getEmail (.getContact eml))
   :additionalInfo (.getAdditionalInfo eml)
   :guid (.getGuid eml)})

(defn xml->map
  "Return map representation of supplied XML string."
  [xml]
  (let [json-obj (XML/toJSONObject xml)]
    (read-json (.toString json-obj))))

(defn ipt-resources
  "Return vector of resource maps from supplied IPT RSS url."
  [url]
  (let [map (xml->map (slurp (io/input-stream url)))]
    (:item (:channel (:rss map)))))

(def zip (partial map vector))
(defn beast-mode
  [m]
  (let [[ks v-colls] (apply zip m)]
    (for [vs (apply zip v-colls)]
         (zipmap ks vs))))

(comment ;; beast-mode example
  (let [partitions {:title ["a" "b" "c"] :link [1 2 3]
                    :name [:aaron :noah :tina]}]
    (beast-mode partitions)))
;; => ({:name :aaron, :link 1, :title "a"}
;;     {:name :noah, :link 2, :title "b"}
;;     {:name :tina, :link 3, :title "c"})

(defn fetch-url
  "Return HTML from supplied URL."
  [url]
  (html/html-resource (java.net.URL. url)))

(defn get-org-uuid
  "Return organization UUID scraped from GBIF registry page at supplied URL:
   http://gbrds.gbif.org/browse/agent?uuid={resource-uuid}"
  [url]
  (try
    (let [nodes (html/select (fetch-url url) [:div.listItem :a])
          urls (map #(html/attr-values % :href) nodes)
          path (first (first urls))
          uuid (second (s/split path #"="))]
      uuid)
    (catch Exception e
      (prn "Whoops can't get UUID for: " url)
      nil)))

(defn uuid->organization
  "Return organization map for supplied organization uuid."
  [uuid]
  (try
    (let [url (format "http://gbrds.gbif.org/registry/organisation/%s.json" uuid)]
      (read-json (slurp (io/input-stream url))))
    (catch Exception e
      (prn "Whoops no org for UUID: " uuid)
      nil)))

;; (defn harvest
;;   [bootstrap]
;;   (for [url (resource-urls resource-table)]
;;     (let [{:keys [resource dataset organization]} (url->metadata url)]
;;       (if bootstrap
;;         (do
;;           (sink-metadata resource dataset organization)
;;           (sink-data resource) ;; TODO
;;           (update-pubdate resource-table url (:pubDate resource)))
;;         (if (not= (:pubDate resource) (pubdate resource-table url))
;;           (do
;;             (sink-metadata resource dataset organization)
;;             (sink-data resource) ;; TODO
;;             (update-pubdate resource-table url (:pubDate resource))))))))

(defprotocol IResource
  "Protocol for a resource."
  (get-props [this] "Get resource properties as a map.")
  (get-dataset [this] "Get resource dataset properties as a map.")
  (get-organization [this] "Get resource organization properties as a map.")
  (get-recs [this] "Get sequence of resource record properties as maps.")
  (get-insert-sql [this table] "Get insert SQL to load resource to CartoDB.")
  (cdb-insert [this table] "Insert resource to supplied table on CartoDB"))

(defrecord Resource
  [url]
  IResource
  (get-props
    [this]
    (let [rss-url (s/replace url "resource" "rss")
          resources (ipt-resources rss-url)
          resource (first (filter #(= url (:link %)) resources))
          guid (:content (:guid resource))
          resource (dissoc resource :guid)
          resource (assoc resource :guid guid)
          [keys vals] (apply zip resource)
          keys (map #(keyword (s/lower-case (last (s/split (name %) #":")))) keys)
          resource (zipmap keys vals)]
      resource))
  (get-dataset
    [this]
    (let [eml-url (s/replace url "resource" "eml")
          eml (EmlFactory/build (io/input-stream eml-url))
          dataset (eml->dataset eml)]
      dataset))
  (get-organization
    [this]
    (let [dataset-guid (:guid (get-dataset this))
            dataset-url (format "http://gbrds.gbif.org/browse/agent?uuid=%s"
                                dataset-guid)
            uuid (get-org-uuid dataset-url)
            organization (uuid->organization uuid)]
        organization))
  (get-recs
    [this]
    (let [dwca-url (s/replace url "resource" "archive")
          dwc-records (dwca/open dwca-url)
          records (map fields dwc-records)]
      records))
  (get-insert-sql
    [this table]
    (let [resource (get-props this)
          dataset (get-dataset this)
          resource (assoc resource :datasetguid (:guid dataset))
          resource (dissoc resource :guid)
          [k v] (apply zip resource)
          k (map #(s/lower-case (last (s/split (name %) #":"))) k)
          v (map #(format "'%s'" (s/replace % "'" "''")) v)
          query "INSERT INTO %s (%s) VALUES (%s);"
          cols (reduce #(str %1 "," %2) k)
          vals (reduce #(str %1 "," %2) v)
          sql (format query table cols vals)]
      sql))
  (cdb-insert
    [this table]
    (let [sql (get-insert-sql this table)]
      (cartodb/query sql "vertnet" :api-key api-key))))

(defn harvest
  "Harvest new records from supplied vector of resource urls and insert into
  resource table."
  [url path]
  (let [r (Resource. url)
        resource (get-props r)
        dataset (get-dataset r)
        organization (get-organization r)
        dataset-id (sink-metadata path resource dataset organization)]
    (do
      (sink-data path resource dataset-id)
      (cdb-insert r "resource"))))

(defn harvest-all
  []
  (let [sql "select link from vn_resources where ipt=true"
        urls (map :link (:rows (cartodb/query sql "vertnet" :api-key api-key)))]
    (prn urls)
    (doall
     (map #(harvest % "/tmp/vn") urls))))

(defn copy-resource-table
  "Copy records from vn_resources to resource_tmp table."
  []
  (let [sql "insert into resource_tmp (link, ipt) (select distinct(link), ipt from vn_resources where ipt = true)"]
    (cartodb/query sql "vertnet" :api-key api-key)))



(defn harvest-updated
  "Harvest updated records from supplied vector of resource urls and update
  resource table."
  [urls])

;; (defn sync-resources
;;   "Return new, updated, deleted resource urls."
;;   [& {:keys [bootstrap] :or {bootstrap false}}]
;;   (cartodb/query "delete from resource_tmp" "vertnet" :api-key api-key)
;;   (if-not bootstrap
;;     (copy-resource-table)) ;; copy rows from vn_resources to resource_tmp.
;;   (let [resource-urls (resource-urls resource-table)
;;         ;resources (map #(Resource. %) resource-urls)
;;         ;insert-results (doall (map #(cdb-insert % "resource_tmp") resources))
;;         new (map #(:link %) (sync-new resource-table "resource" "resource_tmp"))
;;         updated (map #(:link %) (sync-updated resource-table "resource" "resource_tmp"))
;;         deleted (map #(:link %) (sync-deleted resource-table "resource" "resource_tmp"))]
;;     (doall
;;       (map #(harvest-new % "/tmp/vn") new))))


;; TODO
;; sink edges
;;
;; get new/updated/deleted resources from CartoDB:
;; sink new/updated
;; sink edges!
;; figure out when to consolidate pail
;;
