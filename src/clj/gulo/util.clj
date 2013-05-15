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

(defn gen-uuid
  "Return a randomly generated UUID string."
  [& x]
  (str (java.util.UUID/randomUUID)))

(defn wkt-point
  "Return point encoded as WKT (well known text)."
  [lat lng]
  [(str "POINT(" lng " " lat ")")])

;; Valid ranges for latitude and longitude.
(def latlon-range {:lat-min -90 :lat-max 90 :lon-min -180 :lon-max 180})

(defn read-latlon
  "Converts lat and lon values from string to number."
  [lat lon]
  {:pre [(every? string? [lat lon])]}
  (map read-string [lat lon]))

(defn name-valid?
  "Return true if name is valid, otherwise return false."
  [rec]
  (let [index (dwca/index-of rec :scientificname)
        name (nth (field-vals rec) index)]
    (and (not= name nil) (not= (.trim name) ""))))

(defn latlon-valid?
  "Return true if lat and lon are valid, otherwise return false."
  ([^DarwinCoreRecord rec]
     (let [lat (nth (field-vals rec) (dwca/index-of rec :decimallatitude))
           lon (nth (field-vals rec) (dwca/index-of rec :decimallongitude))]
       (apply latlon-valid? (map str [lat lon]))))
  ([lat lon]
     (try
       (let [{:keys [lat-min lat-max lon-min lon-max]} latlon-range
             [lat lon] (read-latlon lat lon)]
         (and (<= lat lat-max)
              (>= lat lat-min)
              (<= lon lon-max)
              (>= lon lon-min)))
       (catch Exception e false))))

(defn splitline
  "Returns vector of line values by splitting on tab."
  [line]
  (vec (.split line "\t")))

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

(defn remove-line-breaks
  [s]
  (if (nil? s)
    ""
    (-> s
        (s/replace "\n" " ")
        (s/replace "\r" " "))))
