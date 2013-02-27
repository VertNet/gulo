(ns gulo.ipt
  "This namespace handles working with IPT."
  (:use [clojure.java.io :as io]
        [cascalog.api]
        [net.cgrand.enlive-html :as html :only (html-resource, select, attr-values)]
        [clojure.data.json :only (read-json)]
        [cartodb.core :as cartodb]
        [gulo.thrift :as t]
        [gulo.hadoop.pail :as p])
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.contrib.zip-filter.xml :as z])
  (:import [java.io File ByteArrayInputStream]
             [org.gbif.metadata.eml EmlFactory]))
(comment
  "Pail layout:

pail/edge/ResourceDatasetEdge/
pail/edge/DatasetRecordEdge/
pail/edge/ResourceOrganizationEdge/
pail/prop/ResourceProperty/.../
pail/prop/DatasetProperty/.../
pail/prop/OrganizationProperty/.../
pail/prop/RecordProperty/{Taxon | Location | ...}"
  
  ;; WIP
  (defn sync
    []
    (for [url (resource-urls)]
      (let [{:keys [resource dataset organization]} (url->metadata url)]
        (if (not= (:pubDate resource) (get-pub-date url))
          (sync-metadata resource dataset organization)
          (sync-data resource)
          (update-resource-table-pubdate resource)))))

  ;; NEXT STEPS:
  ;; 1. Modify pail.clj to vertically partition RecordProperty
  ;; 2. Change detection algo for Records
  
  )
  

(defn sync-metadata
  "TODO: Delete pail first?"
  [resource dataset organization]
  (let [resource-data (t/resource-data resource)
        resource-q (<- [?d] (resource-data ?d))
        dataset-data (t/dataset-data dataset)
        dataset-q (<- [?d] (dataset-data ?d))
        organization-data (t/organization-data organization)
        organization-q (<- [?d] (organization-data ?d))]
    (p/to-pail "/tmp/pail" resource-q)
    (p/to-pail "/tmp/pail" dataset-q)
    (p/to-pail "/tmp/pail" organization-q)))
  
(defn get-pub-date
    [url]
    (let [sql (format "SELECT pubdate FROM resource WHERE url = '%s'" url)]
      (first (map :pubdate (:rows (cartodb/query sql "vertnet" :api-version "v1"))))))

(defn resource-urls
  "Return sequence of IPT resource URLs from CartoDB resource table."
  []
  (let [sql "SELECT url FROM resource WHERE ipt = true"]
    (map :url (:rows (cartodb/query sql "vertnet" :api-version "v1")))))

;; TODO
(defn check-resources
  "Return sequence of IPT resource URLs that have changed by comparing the
   pubDate in the RSS feed for supplied IPT resource URLs with the pubdate in
   CartoDB resource table. Also update CartoDB resource.pubDate if needed."
  [urls])

(defn get-eml
  "Return org.gbif.metadata.eml.Eml object from supplied URL."
  [url]
  (EmlFactory/build (io/input-stream url)))

(defn eml->dataset-property-value-map
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

(def zip (partial map vector))

(defn beast-mode
  [m]
  (let [[ks v-colls] (apply zip m)]
    (for [vs (apply zip v-colls)]
         (zipmap ks vs))))

(defn get-feed
  "Return map representation of RSS feed from supplied URL."
  [url]  
  (let [xml (slurp (io/reader url))
        stream (ByteArrayInputStream. (.getBytes (.trim xml)))
        map (xml/parse stream)
        feed (zip/xml-zip map)]
    feed))

(defn feed-vals
  [feed & tags]
  "Return feed values for supplied tags."
  (apply z/xml-> feed (conj (vec tags) z/text)))

(defn feed->resource-property-value-maps
  "Return sequence of ResourcePropertyValue maps from supplied feed."
  [feed]
  (let [f (fn [key] (feed-vals feed :channel :item key))
        keys [:title :link :description :author :ipt:eml :dc:publisher
              :dc:creator :ipt:dwca :pubDate :guid] ;; TODO add :giud
        props [:title :url :description :author :emlUrl :publisher
               :creator :dwcaUrl :pubDate :guid] ;; TODO add :guid
        vals (map #(f %) keys)
        partitions (apply hash-map (interleave props vals))]
    (beast-mode partitions)))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn get-organization-uuid
  "Return organization UUID scraped from GBIF registry page at supplied URL:
   http://gbrds.gbif.org/browse/agent?uuid={resource-uuid}"
  [url]
  (let [nodes (html/select (fetch-url url) [:div.listItem :a])
        urls (map #(html/attr-values % :href) nodes)
        path (first (first urls))
        uuid (second (s/split path #"="))]
    uuid))

(defn uuid->organization
  [uuid]
  (let [url (format "http://gbrds.gbif.org/registry/organisation/%s.json" uuid)]
    (read-json (slurp (io/input-stream url)))))

(defn url->dataset
  "Return DatasetPropertyValue map from supplied IPT resource URL."
  [url]
  (let [title (second (s/split url #"="))
        eml_url (s/replace url "resource" "eml")
        eml (get-eml eml_url)
        dataset (eml->dataset-property-value-map eml)]
    dataset))

(defn url->metadata
  "Return map of :resource, :dataset, and :organization properties for supplied
  resource URL."
  [url]
  (let [title (second (s/split url #"="))
        rss-url (s/replace url "resource" "rss")
        feed (get-feed rss-url)
        resources (feed->resource-property-value-maps feed)
        x (def resources resources)
        resource (first (filter #(= url (:url %)) resources))
        dataset (url->dataset url)
        x (def dataset dataset)
        guid (:guid resource)        
        organization (if guid
                       (do
                         (let [gbif-url (format "http://gbrds.gbif.org/browse/agent?uuid=%s" guid)]
                               (uuid->organization (get-organization-uuid gbif-url)))))]
    {:resource resource :dataset dataset :organization organization}))

(defn get-resources
  "Return sequence of resource maps {:resource :dataset :organization} from
   supplied sequence of IPT resource URLs of the form:
   http://{host}/ipt/resource.do?r={resource_name}"
  [& {:keys [urls] :or {urls (resource-urls)}}]
  (map #(url->metadata %) urls))

(comment
  (let [f (get-feed "http://ipt.vertnet.org:8080/ipt/rss.do")
        titles (feed-vals f :channel :item :title)]
    (prn titles)))

(comment
  (let [partitions {:title ["a" "b" "c"] :links [1 2 3] :names [:aaron :noa :tina]}]
    (beast-mode partitions))) ;; => ({:title "a", :links 1} {:title "b", :links 2}
