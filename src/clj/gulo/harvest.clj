(ns gulo.harvest
  "This namespace provides functions for downloading a set of Darwin Core
  Archives via URL and saving all records within them to a single tab delineated
  text file."
  (:use [gulo.util :as util :only (gen-uuid)]
        [dwca.core :as dwca]
        [cartodb.client :as cdb :only (query)]
        [clojure.data.csv :as csv]
        [clojure.java.io :as io])
  (:import [org.gbif.dwc.record DarwinCoreRecord]
           [com.google.common.io Files]))

(defn dwca-urls
  "Return vector of Darwin Core Archive URLs as strings."
  []
  (vec (map #(first (vals %)) (cdb/query "vertnet" "SELECT dwca_url FROM publishers"))))

(defn prepend-uuid
  "Return vector of supplied DarwinCoreRecord values with a UUID prepended."
  [^DarwinCoreRecord rec]
  (cons (util/gen-uuid) (field-vals rec)))

(defn url->csv
  "Convert Darwin Core Archive at supplied URL into tab delimited file at path."
  [path url]
  (let [records (dwca/open url)]
    (with-open [f (io/writer path :append true)]
      (csv/write-csv f (map prepend-uuid records) :separator \tab))))

(defn harvest
  "Harvest Darwin Core Archives from URLs into a tab delimited file at path."
  [& {:keys [urls path] 
      :or {urls (dwca-urls) path (str (->> (Files/createTempDir) .getPath) "/dwc.csv")}}]
  (println path)
  (map (partial url->csv path) urls))

