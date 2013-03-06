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
        [dwca.core :as dwca])
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.contrib.zip-filter.xml :as z])
  (:import [java.io File ByteArrayInputStream]
           [org.gbif.metadata.eml EmlFactory]
           [org.json XML]))

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
    resource: {:creator :author :emlUrl :guid :title :url :pubDate :publisher
               :description :dwcaUrl}                            
    dataset: {:title :creator :metadataProvider :language :associatedParty
              :pubDate :contact :additionalInfo}
    organization: {:primaryContactType :nodeName :primaryContactDescription :key
                   :name :primaryContactAddress :primaryContactName :nameLanguage
                   :primaryContactPhone :homepageURL :descriptionLanguage
                   :description :nodeKey :nodeContactEmail :primaryContactEmail}"
  [pail resource dataset organization]
  (let [resource-data (t/resource-data resource)
        resource-q (<- [?d] (resource-data ?d))
        dataset-data (t/dataset-data dataset)
        dataset-q (<- [?d] (dataset-data ?d))
        organization-data (t/organization-data organization)
        organization-q (<- [?d] (organization-data ?d))]
    (p/to-pail pail resource-q)
    (p/to-pail pail dataset-q)
    (p/to-pail pail organization-q)))

(defn sink-data
  [pail resource]
  (let [dataset-guid (:guid resource)
        dwca-url (:dwca resource)
        dwc-records (dwca/open dwca-url)
        records (map fields dwc-records)
        record-data (map (partial t/record-data dataset-guid) records)]
    (for [d record-data]
      (p/to-pail pail (<- [?d] (d ?d))))))

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
    (let [template "SELECT %s.* FROM %s LEFT OUTER JOIN %s USING (guid) WHERE %s.guid is null" 
          sql (format template tmp tmp table table)]
      (:rows (cartodb/query sql "vertnet" :api-key api-key))))
  (sync-updated
    [_ table tmp]
    (let [template "SELECT %s.* FROM %s, %s WHERE %s.guid = %s.guid AND %s.pubdate <> %s.pubdate"
          sql (format template tmp tmp table tmp table tmp table)]
      (:rows (cartodb/query sql "vertnet" :api-key api-key))))
  (sync-deleted
    [_ table tmp]
    (let [template "SELECT %s.* FROM %s LEFT OUTER JOIN %s USING (guid) WHERE %s.guid is null"
          sql (format template table table tmp tmp)]
      (:rows (cartodb/query sql "vertnet" :api-key api-key)))))

(defn resource-table-response
  "Return CartoDB response JSON for all resource table records."
  []
  (cartodb/query "SELECT * FROM resources" "vertnet" :api-version "v1" :api-key api-key))

;; The resource table.
(def resource-table (ResourceTable. (resource-table-response)))

(defn url->eml
  "Return org.gbif.metadata.eml.Eml object from supplied EML URL."
  [url]
  (EmlFactory/build (io/input-stream url)))

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
   :additionalInfo (.getAdditionalInfo eml)})

(defn xml->map
  "Return map representation of supplied XML string."
  [xml]
  (let [json-obj (XML/toJSONObject xml)]
    (read-json (.toString json-obj))))

(defn vertnet-ipt-resources
  "Return vector of IPT resource maps taken from RSS feed."
  []
  (let [map (xml->map (slurp (io/input-stream vertnet-ipt-rss)))]
    (:item (:channel (:rss map)))))

(def zip (partial map vector))
(defn beast-mode
  [m]
  (let [[ks v-colls] (apply zip m)]
    (for [vs (apply zip v-colls)]
         (zipmap ks vs))))

;; beast-mode example:
(comment 
  (let [partitions {:title ["a" "b" "c"] :link [1 2 3]
                    :name [:aaron :noah :tina]}]
    (beast-mode partitions)))
;; => ({:name :aaron, :link 1, :title "a"}
;;     {:name :noah, :link 2, :title "b"}
;;     {:name :tina, :link 3, :title "c"})

(defn fix-keys
  "Return supplied resource map with fixed keys lower cased.

  The resource map is created from an RSS feed. The following keys:

    :dc:publisher :ipt:dwca :dc:creator :ipt:eml

  Are changed to:

    :publisher :dwca :creator :eml

  Sometimes a :guid isn't in the RSS feed, so we add that key when needed.
  "
  [resource]
  (let [[ks vs] (apply zip resource)
        vs (map #(s/replace % "'" "\"") vs)
        vs (map #(if (or (nil? %) (s/blank? %)) "'NONE'" %) vs)
        fixed-ks (map #(keyword (s/lower-case (last (s/split (name %) #":")))) ks)
        r (zipmap fixed-ks vs)]
    (if (contains? r :guid)
      (assoc r :guid (:content (:guid r)))
      (assoc r :guid "NONE"))))

(defn url->feedmap
  "Return map representation of RSS feed from supplied URL."
  [url]
  (let [xml (slurp (io/reader url))
        stream (ByteArrayInputStream. (.getBytes (.trim xml)))
        map (xml/parse stream)
        feed (zip/xml-zip map)]
    feed))

(defn feed-vals
  "Return feed values for tags from supplied feedmap."
  [feedmap & tags]
  (apply z/xml-> feedmap (conj (vec tags) z/text)))

(defn feedmap->resources
  "Return sequence of ResourcePropertyValue maps from supplied feed."
  [feedmap]
  (let [f (fn [key] (feed-vals feedmap :channel :item key))
        keys [:title :link :description :author :ipt:eml :dc:publisher
              :dc:creator :ipt:dwca :pubDate :guid] ;; TODO add :giud
        props [:title :url :description :author :emlUrl :publisher
               :creator :dwcaUrl :pubDate :guid] ;; TODO add :guid
        vals (map #(f %) keys)
        partitions (apply hash-map (interleave props vals))]
    (beast-mode partitions)))

(defn fetch-url
  "Return HTML from supplied URL."
  [url]
  (html/html-resource (java.net.URL. url)))

(defn url->org-uuid
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
      (prn "Whoops can't get UUID for" url)
      nil)))

(defn uuid->organization
  "Return organization map for supplied organization uuid."
  [uuid]
  (try
    (let [url (format "http://gbrds.gbif.org/registry/organisation/%s.json" uuid)]
      (read-json (slurp (io/input-stream url))))
    (catch Exception e
      (prn "Whoops no org" uuid)
      nil)))

(defn url->dataset
  "Return DatasetPropertyValue map from supplied IPT resource URL."
  [url]
  (let [title (second (s/split url #"="))
        eml-url (s/replace url "resource" "eml")
        eml (url->eml eml-url)
        dataset (eml->dataset eml)]
    dataset))

(defn url->metadata
  "Return map of :resource, :dataset, and :organization maps.

  Args:
    url: The resource URL.
  "
  [url]
  (let [title (second (s/split url #"="))
        rss-url (s/replace url "resource" "rss")
        feedmap (url->feedmap rss-url)
        resources (feedmap->resources feedmap)
        resource (first (filter #(= url (:url %)) resources))
        dataset (url->dataset url)
        guid (:guid resource)        
        organization (if guid
                       (do
                         (let [gbif-url (format "http://gbrds.gbif.org/browse/agent?uuid=%s" guid)]
                           (uuid->organization (url->org-uuid gbif-url)))))]
    {:resource resource :dataset dataset :organization organization}))

(defn resource->metadata
  [resource] ;; resource map from cartodb
  (let [url (:link resource)
        dataset (url->dataset url)
        guid (:guid resource)        
        organization (if guid
                       (do
                         (let [gbif-url (format "http://gbrds.gbif.org/browse/agent?uuid=%s" guid)]
                           (uuid->organization (url->org-uuid gbif-url)))))]
    {:resource resource :dataset dataset :organization organization}))

(defn get-resources
  "Return sequence of resource maps {:resource :dataset :organization} from
   supplied sequence of IPT resource URLs of the form:
   http://{host}/ipt/resource.do?r={resource_name}"
  [& {:keys [urls] :or {resource-urls (take-last 45 (urls resource-table))}}]
  (map #(url->metadata %) urls))


(defn insert-ipt-resources
  "Creates statement to insert vector of IPT resource maps to supplied
   table."
  [table resource-vec]
  (apply (partial cartodb-utils/maps->insert-sql table) resource-vec))

(defn get-deltas
  [table table-tmp]
  {:new (sync-new resource-table table table-tmp)
   :updated (sync-updated resource-table table table-tmp)
   :deleted (sync-deleted resource-table table table-tmp)})

(defn resource->organization
  "Return resource with :orgname :orgurl :orgcontact if org exists."
  [resource]
  (let [guid (:guid resource)
        url (str "http://gbrds.gbif.org/browse/agent?uuid=" guid)
        uuid (url->org-uuid url)
        org (uuid->organization uuid)]
    (if org
      (let [resource (assoc resource :orgname (:name org))
            resource (assoc resource :orgurl (:homepageURL org))
            resource (assoc resource :orgcontact (:primaryContactEmail org))]
        resource)
      resource)))

(defn resource->sql
  "Return SQL insert statement for supplied resource map from IPT RSS feed."
  [table resource]
  (let [guid (if (contains? resource :guid) (:content (:guid resource)) "NONE")
        resource (assoc resource :guid guid)
        resource (resource->organization resource)
        [k v] (apply zip resource)
        k (map #(s/lower-case (last (s/split (name %) #":"))) k)
        v (map #(format "'%s'" (s/replace % "'" "\"")) v)
        sql "INSERT INTO %s (%s) VALUES (%s);"
        cols (reduce #(str %1 "," %2) k)
        vals (reduce #(str %1 "," %2) v)]
    (format sql table cols vals)))

(defn insert-resources
  "Harvest resources from RSS and insert into CartoDB."
  []
  (let [resources (vertnet-ipt-resources)
        sql (reduce #(str %1 "" %2) (map (partial resource->sql "resources") resources))]
    (cartodb/query sql "vertnet" :api-key api-key)))

(defn harvest
  [bootstrap]
  (for [url (resource-urls resource-table)]
    (let [{:keys [resource dataset organization]} (url->metadata url)]
      (if bootstrap
        (do
          (sink-metadata resource dataset organization)
          (sink-data resource) ;; TODO
          (update-pubdate resource-table url (:pubDate resource)))
        (if (not= (:pubDate resource) (pubdate resource-table url))
          (do
            (sink-metadata resource dataset organization)
            (sink-data resource) ;; TODO
            (update-pubdate resource-table url (:pubDate resource))))))))

(defn test-data
  []
  (let [resource-url "http://ipt.vertnet.org:8080/ipt/resource.do?r=ttrs_mammals"
        sql (format "SELECT author,link,eml,dwca,orgcontact,orgname,orgurl,guid,description,pubdate,publisher,name FROM resources WHERE link = '%s'" resource-url)
        resource (first (:rows (cartodb/query sql "vertnet" :api-key api-key)))
        {:keys [resource dataset organization]} (resource->metadata resource)]
    (sink-metadata "/tmp/vn" resource dataset organization)
    (sink-data "/tmp/vn" resource)))

(comment
  "Pail layout:
    rootpail/edge/ResourceDatasetEdge/
    rootpail/edge/DatasetRecordEdge/
    rootpail/edge/ResourceOrganizationEdge/
    rootpail/prop/ResourceProperty/.../
    rootpail/prop/DatasetProperty/.../
    rootpail/prop/OrganizationProperty/.../
    rootpail/prop/RecordProperty/{Taxon | Location | ...}"
  
  ;; Harvest logic:
  (defn harvest
    [bootstrap]
    (for [url (urls resource-table)]
      (let [{:keys [resource dataset organization]} (url->metadata url)]
        (if bootstrap
          (do
            (sink-metadata resource dataset organization)
            (sink-data resource) ;; TODO
            (update-pubdate resource-table url (:pubDate resource)))
          (if (not= (:pubDate resource) (pubdate resource-table url))
            (sink-metadata "/tmp/vn" resource dataset organization)
            (sink-data resource) ;; TODO
            (update-pubdate resource-table url (:pubDate resource)))))))
  
  ;; NEXT STEPS:
  ;;
  ;; Sink edges hhh
)
