(ns gulo.harvest
  "This namespace handles harvesting Darwin Core Archives."
  (:use [gulo.util :as util :only (gen-uuid, name-valid? latlon-valid?)]
        [clojure.data.json :only (read-json)]
        [cascalog.api]
        [dwca.core :as dwca :only (open field-vals)]
        [cartodb.core :as cartodb]
        [clojure.data.csv :as csv]
        [clojure.java.io :as io])
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.zip :as zip])
  (:import [java.io File]
           [org.gbif.dwc.record DarwinCoreRecord]
           [com.google.common.io Files]
           [com.google.common.base Charsets]))

;; Slurps resources/creds.json for CartoDB creds:
(def cartodb-creds (read-json (slurp (io/resource "creds.json"))))

;; CartoDB API Key:
(def api-key (:api_key cartodb-creds))

;; Slurps resources/s3.json for Amazon S3: {"access-key" "secret-key"}
(def s3-creds (read-json (slurp (io/resource "aws.json"))))

(def zip (partial map vector))

(defn publishers
  "Return vector of maps containing :dwca_url, :inst_code, and :inst_name keys
  for each publisher in the publishers CartoDB table."
  []
  (let [sql "SELECT archive_name, dwca_url, inst_code, inst_name FROM publishers"]
    (:rows (cartodb/query sql "vertnet" :api-version "v1"))))

(defn- prepend-props
  [vals props]
  (concat props vals))

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
    (?- (hfs-textline sink :sinkmode :replace)
        (hfs-textline path))))

(defn csv->s3
  "Uploads all CSV files at supplied path using vector of file names."
  [path names]
  (map #(file->s3 (str path % ".csv") (str "guloharvest/publishers/" %)) names))

(defn archive->csv
  "Convert publisher Darwin Core Archive to tab delineated file at supplied path."
  [path url props & {:keys [s3] :or {s3 false}}]
  (prn (format "Downloading: %s" url))
  (try
    (let [resource-name (second (s/split url #"="))
          uuid (util/gen-uuid)
          csv-path (format "%s/%s-%s.csv" path resource-name uuid)
          archive-url (s/replace url "resource" "archive")
          records (dwca/open archive-url :path path)
          vals (map field-vals records)
          vals (map #(prepend-props % props) vals)
          vals (map #(concat % [";"]) vals)
          out (io/writer (io/file csv-path) :encoding "UTF-8")]
      (do
        (prn (format "Writing to %s" csv-path))
        (with-open [f out]
          (csv/write-csv f vals :separator \tab :quote \"))
        (if s3
          (do
            (prn (format "Uploading %s to S3..." path resource-name))
            (file->s3 csv-path (format "vnproject/data/staging/%s-%s" resource-name uuid))))
        (prn "Done harvesting" resource-name)
        (io/delete-file csv-path)))
    (catch Exception e (prn "Error harvesting" url (.getMessage e)))))

(defn harvest-all
  "Harvest all resource in vn-resources table."
  [& {:keys [path] :or {path "/tmp/vn"}}]
  (let [sql "select link from vn_resources where ipt=true"
        urls (map :link (:rows (cartodb/query sql "vertnet" :api-key api-key)))]
    (doall
     (prn (format "Harvesting %s resources" (count urls)))
     (map #(archive->csv path %) urls))))

(defn harvest
  "Harvest supplied map of publishers in parallel to CSV files at path."
  [publishers path]
  (doall
   (map #(future (archive->csv path %)) publishers)))
