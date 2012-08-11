(ns gulo.harvest
  "This namespace handles harvesting Darwin Core Archives."
  (:use [gulo.util :as util :only (gen-uuid, name-valid? latlon-valid?)]
        [dwca.core :as dwca]
        [cartodb.core :as cartodb]
        [clojure.data.csv :as csv]
        [clojure.java.io :as io])
  (:require [clojure.string :as s])
  (:import [java.io File]
           [org.gbif.dwc.record DarwinCoreRecord]
           [com.google.common.io Files]
           [com.google.common.base Charsets]))

(defn publishers
  "Return vector of maps containing :dwca_url, :inst_code, and :inst_name keys
  for each publisher in the publishers CartoDB table."
  []
  (let [sql "SELECT dwca_url, inst_code, inst_name FROM publishers"]
    (:rows (cartodb/query sql "vertnet"))))

(defn- prepend-uuid
  "Prepend UUID to sequence of vals."
  [vals]
  (cons (util/gen-uuid) vals))

(defn- append-vals
  "Append name and code to sequence of vals."
  [vals name code]
  (conj (vec vals) name code)) ;; vec forces conj to append to tail.

(defn- clean-val
  "Clean val by removing tabs and line breaks and replacing double quotes with
   single quotes."
  [^String val]
  (if val
    (-> val
        (s/replace "\t" " ")
        (s/replace "\n" " ")
        (s/replace "\r" " ")
        (s/replace "\"" "`"))
    val))

(defn- clean
  "Clean a sequence of vals."
  [vals]
  (map clean-val vals))

(defn- valid-rec?
  [rec]
  (and (name-valid? rec) (latlon-valid? rec)))

(defn publisher->file
  "Convert publisher Darwin Core Archive to tab delineated file at supplied path."
  [path publisher]
  (try
    (let [{:keys [dwca_url inst_code inst_name]} publisher
          records (dwca/open dwca_url)
          valid (filter valid-rec? records)
          vals (map field-vals valid)
          vals (map clean vals)
          vals (map prepend-uuid vals)
          vals (map #(append-vals % inst_name inst_code) vals)
          x (Files/newWriterSupplier (File. path) Charsets/UTF_8 true)
          writer (.getOutput x)]
      (with-open [f writer] 
        (csv/write-csv f vals :separator \tab :quote \")))
    (catch Exception e (prn "Error harvesting" publisher (.getMessage e)))))

(defn harvest
  "Harvest Darwin Core Archives from list of publishers to file at supplied path."
  [publishers path]
  (map (partial publisher->file path) publishers))
