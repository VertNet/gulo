(ns gulo.views
  (:use [cascalog.api]
        [gulo.thrift :as t]
        [gulo.hadoop.pail :as p]
        [dwca.core :as dwca])
  (:require [cascalog.ops :as c])
  (:import [backtype.hadoop.pail Pail]))

(def stats-paths
  {:total-records ["prop" "RecordProperty" "Occurrence"]
   :total-publishers ["prop" "OrganizationProperty"]
   :records-by-country ["prop" "RecordProperty" "Location"]
   :records-by-collection ["prop" "RecordProperty" "RecordLevel"]
   :records-by-class ["prop" "RecordProperty" "Taxon"]


;;   :downloaded-last-30-days ["prop"]
;;   :datasets-last-30-days ["prop"]
   :total-taxa ["prop" "RecordProperty" "Occurrence"]})

(defn get-n-sample-records
  "Grab n records from the sample pail."
  [n & [p]]
  (let [path (str "/tmp/vn/" p)]
    (take n (Pail. path))))

(defn get-source-id
  "Unpack Data thrift object and return RecordProperty's UUID."
  [obj]
  (->> obj .dataUnit .getRecordProperty .getId .getRecordSource .getSourceID))

(defn get-organization-id
  "Unpack Data thrift object and return OrganizationProperty's UUID."
  [obj]
  (->> obj .dataUnit .getOrganizationProperty .getId .getUuid))

(defn get-country
  "Unpack Data thrift object and return RecordProperty's country."
  [obj]
  (->> obj .dataUnit .getRecordProperty .getValue .getLocation .getCountry))

(defn get-unique-occurrences
  "Based on Data object RecordProperty's UUID, return unique Data objects."
  [src]
  (<- [?obj]
      (src _ ?obj)
      (get-source-id ?obj :> ?id)
      (:distinct)))

(defn get-unique-publishers
  "Based on Data object OrganizationProperty's UUID, return unique
   Data objects."
  [src]
  (<- [?id]
      (src _ ?obj)
      (get-organization-id ?obj :> ?id)
      (:distinct)))

(defn total-by-country-query
  "Count unique records by country."
  [src]
  (let [uniques (get-unique-occurrences src)]
    (<- [?country ?count]
        (uniques ?obj)
        (get-country ?obj :> ?country)
        (c/count ?count))))

(defn total-occurrences-query
  "Count total unique occurrences."
  [src]
  (let [uniques (get-unique-occurrences src)]
    (<- [?count]
        (uniques ?obj)
        (c/count ?count))))

(defn total-publishers-query
  "Count total publishers."
  [src]
  (let [uniques (get-unique-publishers src)]
    (<- [?count]
        (uniques ?obj)
        (c/count ?count))))

(comment
  ;; totals by country
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:records-by-country stats-paths))]
    (??- (total-by-country-query tap)))

  ;; total records
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:total-records stats-paths))]
    (??- (total-occurrences-query tap)))

  ;; total publishers
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:total-publishers stats-paths))]
    (??- (totals-publishers-query tap)))
  )
