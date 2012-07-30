(ns gulo.harvest
  "This namespace handles harvesting Darwin Core Archives."
  (:use [gulo.util :as util :only (gen-uuid)]
        [dwca.core :as dwca]
        [cartodb.core :as cartodb]
        [clojure.data.csv :as csv]
        [clojure.java.io :as io])
  (:require [clojure.string :as s])
  (:import [java.io File]
           [org.gbif.dwc.record DarwinCoreRecord]
           [com.google.common.io Files]
           [com.google.common.base Charsets]))

(defn dwca-urls
  "Return vector of Darwin Core Archive URLs as strings."
  []
  (let [rows (:rows (cartodb/query "SELECT dwca_url FROM publishers" "vertnet"))]
    (vec (map #(first (vals %)) rows))))

(defn prepend-uuid
  "Return vector of supplied DarwinCoreRecord values with a UUID prepended."
  [^DarwinCoreRecord rec]
  (cons (util/gen-uuid) (field-vals rec)))

(defn fix-val
  "Returns string val with tabs and line breaks removed. Also replaces double
  quotes with a single quote."
  [^String val]
  (if val
    (-> val
        (s/replace "\t" " ")
        (s/replace "\n" " ")
        (s/replace "\r" " ")
        (s/replace "\"" "'"))
    val))

(defn clean-vals
  "Clean sequence of Darwin Core record values with special characters removed."
  [^DarwinCoreRecord rec]
  (map fix-val (prepend-uuid rec)))

(defn url->csv
  "Convert Darwin Core Archive at supplied URL into tab delimited file at path."
  [path url]
  (try
    (let [records (dwca/open url)
          lines (map clean-vals records)
          x (Files/newWriterSupplier (File. path) Charsets/UTF_8 true)
          writer (.getOutput x)]
      (with-open [f writer] 
        (csv/write-csv f lines :separator \tab :quote \")))
    (catch Exception e (prn "Error harvesting" url (.getMessage e)))))

(defn harvest
  "Harvest Darwin Core Archives from URLs into a tab delimited file at path."
 [urls path] 
 (map (partial url->csv path) urls))
