(ns gulo.harvest
  "This namespace handles harvesting Darwin Core Archives."
  (:use [gulo.util :as util :only (gen-uuid, name-valid? latlon-valid?)]
        [clojure.data.json :only (read-json)]
        [cascalog.api]
        [dwca.core :as dwca]
        [cartodb.core :as cartodb]
        [clojure.data.csv :as csv]
        [clojure.java.io :as io])
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (:import [java.io File]
           [org.gbif.dwc.record DarwinCoreRecord]
           [com.google.common.io Files]
           [com.google.common.base Charsets]))

;; Slurps resources/s3.json for Amazon S3: {"access-key" "secret-key"}
(def s3-creds (read-json (slurp (io/resource "s3.json"))))

(defn publishers
  "Return vector of maps containing :dwca_url, :inst_code, and :inst_name keys
  for each publisher in the publishers CartoDB table."
  []
  (let [sql "SELECT dwca_url, inst_code, inst_name FROM publishers"]
    (:rows (cartodb/query sql "vertnet" :api-version "v1"))))

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

(defn file->s3
  "Upload files at supplied path to S3 path."
  [path s3path]
  (let [key (:access-key s3-creds)
        secret (:secret-key s3-creds)
        sink (str "s3n://" key  ":" secret "@" s3path)]
    (prn sink)
    (?- (hfs-textline sink :sinkmode :replace)
        (hfs-textline path))))

(defn publisher->file
  "Convert publisher Darwin Core Archive to tab delineated file at supplied path."
  [path publisher]
  (prn "Harvesting:" :dwca_url publisher)
  (try
    (let [{:keys [dwca_url inst_code inst_name]} publisher
          name (dwca/archive-name dwca_url)
          path (str path "/" name ".csv")
          records (dwca/open dwca_url)
          valid (filter valid-rec? records)
          vals (map field-vals valid)
          vals (map clean vals)
          vals (map prepend-uuid vals)
          vals (map #(append-vals % inst_name inst_code) vals)
          out (io/writer (io/file path) :encoding "UTF-8")]
      (with-open [f out]
        (prn "Processing" path)
        (csv/write-csv f vals :separator \tab :quote \"))
      (file->s3 path (str "guloharvest/" name)))
    (catch Exception e (prn "Error harvesting" (:dwca_url publisher) (.getMessage e)))))

(defn harvest
  "Harvest supplied map of publishers in parallel to CSV files at path."
  [publishers path]
  (doall
   (map #(future (publisher->file path %)) publishers)))
