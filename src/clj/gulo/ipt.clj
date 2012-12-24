(ns gulo.ipt
  "This namespace handles working with IPT."
  (:use [feedparser-clj.core :as rss]
        [clojure.java.io :as io])
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.contrib.zip-filter.xml :as z])
  (:import [java.io File ByteArrayInputStream]
             [org.gbif.metadata.eml EmlFactory]))

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
   :associatedParty (vec (for [x (.getAssociatedParties eml)] (.getEmail x)))
   :pubDate (.toString (.getPubDate eml))
   :contact (.getEmail (.getContact eml))
   :additionalInfo (.getAdditionalInfo eml)})

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

(defn beast-mode
  "Return sequence of maps that transpose the supplied vertical partitions."
  [partitions]
  (let [n (count partitions)
        keys (keys partitions)
        vals (vals partitions)
        keywords (for [k keys] (for [x (range (+ n 1))] k))
        allkeys (apply interleave keywords)
        allvals (apply interleave vals)
        all (interleave allkeys allvals)
        maps (map #(apply hash-map %) (partition-all (* 2) all))]
    (map #(apply merge %) (partition-all (+ n 1) maps))))

(defn feed->resource-property-value-maps
  "Return sequence of ResourcePropertyValue maps from supplied feed."
  [feed]
  (let [f (fn [key] (feed-vals feed :channel :item key))
        keys [:title :link :description :author :ipt:eml :dc:publisher
              :dc:creator :ipt:dwca :pubDate :guid]
        props [:title :url :description :author :emlUrl :publisher
               :creator :dwcaUrl :pubDate :guid]
        vals (map #(f %) keys)        
        partitions (apply hash-map (interleave props vals))]
    (beast-mode partitions)))

(defn get-dataset
  "Return DatasetPropertyValue map from supplied IPT resource URL."
  [url]
  (let [title (second (s/split url #"="))
        eml_url (s/replace url "resource" "eml")
        eml (get-eml eml_url)
        dataset (eml->dataset-property-value-map eml)]
    dataset))

(defn get-resource
  "Return map with :dataset => DatasetPropertyValue map and :resource =>
   ResourcePropertyValue map from supplied IPT resource URL."
  [url]
  (let [title (second (s/split url #"="))
        rss_url (s/replace url "resource" "rss")
        feed (get-feed rss_url)
        resources (feed->resource-property-value-maps feed)
        resource (first (filter #(= url (:url %)) resources))
        dataset (get-dataset url)]
    {:resource resource :dataset dataset}))

(defn get-resources
  "Return sequence of resource maps {:resource :dataset} from supplied sequence
   of IPT resource URLs."
  [urls]
  (map #(get-resource %) urls))

(comment
  (let [f (get-feed "http://ipt.vertnet.org:8080/ipt/rss.do")
        titles (feed-vals f :channel :item :title)]
    (prn titles)))

(comment
  (let [partitions {:title ["a" "b" "c"] :links [1 2 3]}]
    (beast-mode partitions))) ;; => ({:title "a", :links 1} {:title "b", :links 2}

