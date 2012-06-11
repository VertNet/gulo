(ns gulo.core
  "This namespace downloads and harvests a set of Darwin Core Archives using
  Cascalog and unicorn magic."
  (:use [cascalog.api]
        [dwca.core]
        [cartodb.client :only (query)]
        [clojure.string :only (join split)])
  (:require [clojure.java.io :as io])
  (:import [org.gbif.dwc.record DarwinCoreRecord]
           [java.lang.reflect Field]
           [com.google.common.io Files]))

(defn dwca-urls
  "Return collection of Darwin Core Archive URLs."
  []
(vec (map #(vals %) (query "vertnet" "SELECT dwca_url FROM publishers"))))
    
(defn archive-name
  "Return archive name from supplied URL as defined by the IPT."
  [url]
  (str "dwca-" (nth (split url #"=") 1)))

(defn field-val
  "Return the string value of the supplied record field."
  [^Field field ^DarwinCoreRecord rec]
  {:pre [(instance? Field field)
         (instance? DarwinCoreRecord rec)]}
  (.setAccessible field true)
  (let [val (.get field rec)]
    (cond val (.trim val))))

(defn rec->lines
  "Return a tab dilinated string of values in supplied DarwinCoreRecord object."
  [^ DarwinCoreRecord rec]
  {:pre [(instance? DarwinCoreRecord rec)]}
  (let [fields (->> rec .getClass .getDeclaredFields)
        values (map #(field-val % rec) fields)]
    (join "\t" values)))

(defn grab
  "Download and expand a Darwin Core Archive at a URL and return a path to it."
  [url]
  (let [temp-dir (Files/createTempDir)
        temp-path (.getPath temp-dir)
        archive-name (archive-name url)
        zip-path (str temp-path "/" archive-name ".zip")
        archive-path (str temp-path "/" archive-name)]
    (download url zip-path)
    (unzip zip-path archive-path)
    archive-path))
  
(defmapcatop url->recs
  "Emit records as tab delineated lines from archive located at URL."
  [url]
  (for [rec (get-records (grab url))]
      [(rec->lines rec)]))

(defn harvest
  "Download and store records from many Darwin Core Archive URLs to CSV file."
  [sink-path]
  (let [source (dwca-urls)
        sink (hfs-delimited sink-path :delimiter "\t" :sinkmode :replace)]
    (?<- sink
         [?line]       
         (source ?url)
         (url->recs ?url :> ?line))))
