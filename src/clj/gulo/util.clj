(ns gulo.util
  "This namespace contains utility functions."
  (:use [dwca.core :as dwca :only (index-of field-vals)]
        [clojure.data.json :only (read-json)])
  (:import [org.gbif.dwc.record DarwinCoreRecord])
  (:require [clj-time.core :as time]
            [clj-time.format :as f]
            [clojure.string :as s]
            [clojure.java.io :as io]))

;; Slurps resources/creds.json for CartoDB OAuth: {"key" "secret" "user" "password"}
(def cartodb-creds (read-json (slurp (io/resource "creds.json"))))

;; CartoDB API key:
(def api-key (:api_key cartodb-creds))

;; Slurps resources/aws.json for Amazon S3: {"access-key" "secret-key"}
(def aws-creds (read-json (slurp (io/resource "aws.json"))))

(defn todays-date
  "Returns current date as \"YYYY-MM-dd\".

   Usage:
     (todays-date)
     ;=> \"2013-04-16\""
  []
  (f/unparse (f/formatters :year-month-day) (time/now)))

(defn mk-stats-out-path
  "Build output path, using a base path, query name, and the current date.

   Usage:
     (mk-stats-out-path \"/tmp/stats\" \"taxon\")
     ;=> \"/tmp/stats/2013-04-17/taxon\""
  [base-path date-str query-name]
  (format "%s/%s/%s" base-path date-str query-name))

(defn line-breaks->spaces
  [s]
  (if (nil? s)
    ""
    (-> s
        (s/replace "\n" " ")
        (s/replace "\r" " "))))

(defn resource-url->name
  [url]
  (second (s/split url #"=")))

(defn mk-csv-fname
  [resource-name uuid]
  (format "%s-%s.csv" resource-name uuid))

(defn mk-local-path
  [path resource-name uuid]
  (format "%s/%s" path (mk-csv-fname resource-name uuid)))

(defn mk-s3-path
  [bucket s3-path resource-name uuid]
  (format "%s/%s/%s" bucket s3-path (mk-csv-fname resource-name uuid)))

(defn mk-full-s3-path
  [bucket s3-path resource-name uuid & [creds-map]]
  (let [aws-creds (or creds-map aws-creds)
        key (:access-key aws-creds)
        secret (:secret-key aws-creds)
        path (mk-s3-path bucket s3-path resource-name uuid)]
    (format "s3n://%s:%s@%s" key secret path)))

(defn resource-url->archive-url
  [url]
  (s/replace url "resource" "archive"))

(defn resource-url->eml-url
  [url]
  (s/replace url "resource" "eml"))
