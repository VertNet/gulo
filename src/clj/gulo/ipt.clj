(ns gulo.ipt
  "This namespace handles working with IPT."
  (:use [clojure.data.json :only (read-json)]
        [cascalog.api]
        [feedparser-clj.core :as rss]
        [clojure.java.io :as io])
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.contrib.zip-filter.xml :as z])
  (:import [java.io File ByteArrayInputStream]
           [org.gbif.dwc.record DarwinCoreRecord]
           [com.google.common.io Files]
           [com.google.common.base Charsets]
           [org.gbif.metadata.eml EmlFactory]))

(defn get-eml
  [url]
  "Return org.gbif.metadata.eml.Eml object from supplied URL."
  (EmlFactory/build (io/input-stream url)))

(defn eml->dataset-property-value-map
  [eml]
  "Return DatasetPropertyValue map from supplied org.gbif.metadata.eml.Eml obj."
  {:title (.getTitle eml)
   :creator (.getCreatorEmail eml)
   :metadataProvider (.getEmail (.getMetadataProvider eml))
   :language (.getLanguage eml)
   :associatedParty (vec (for [x (.getAssociatedParties eml)] (.getEmail x)))
   :pubDate (.toString (.getPubDate eml))
   :contact (.getEmail (.getContact eml))
   :additionalInfo (.getAdditionalInfo eml)})

(defn get-feed
  [url]
  "Return map representation of RSS feed from supplied URL."
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
  [partitions]
  "Return sequence of maps that transpose the supplied vertical partitions."
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
  [feed]
  "Return sequence of ResourcePropertyValue maps from supplied feed."
  (let [f (fn [key] (feed-vals feed :channel :item key))
        keys [:title :link :description :author :ipt:eml :dc:publisher
              :dc:creator :ipt:dwca :pubDate :guid]
        props [:title :url :description :author :emlUrl :publisher
               :creator :dwcaUrl :pubDate :guid]
        vals (map #(f %) keys)        
        partitions (apply hash-map (interleave props vals))]
    [partitions (beast-mode partitions)]))

(comment
  (let [f (get-feed "http://ipt.vertnet.org:8080/ipt/rss.do")
        titles (feed-vals f :channel :item :title)]
    (prn titles)))

(comment
  (let [partitions {:title ["a" "b" "c"] :links [1 2 3]}]
    (beast-mode partitions))) ;; => ({:title "a", :links 1} {:title "b", :links 2}

