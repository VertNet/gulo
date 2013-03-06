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
   :total-taxa ["prop" "RecordProperty" "Taxon"]})

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

(defn get-scientific-name
  [obj]
  (->> obj .dataUnit .getRecordProperty .getValue .getTaxon .getScientificName))

(defn get-unique-sci-names
  "Unpack RecordPropertyValue Data objects and return unique
  scientific names."  [src]
  (<- [?scientific-name]
      (src _ ?obj)
      (get-scientific-name ?obj :> ?scientific-name)
      (:distinct true)))

(defn get-unique-occurrences
  "Unpack RecordProperty Data objects and return unique
  occurrence ids."  [src]
  (<- [?id]
      (src _ ?obj)
      (get-source-id ?obj :> ?id)
      (:distinct true)))

(defn get-unique-occ-by-country
  "Unpack RecordProperty Data objects and return
   unique [?id ?country] tuples."
  [src]
  (<- [?id ?country]
      (src _ ?obj)
      (get-source-id ?obj :> ?id)
      (get-country ?obj :> ?country)
      (:distinct true)))

(defn get-unique-publishers
  "Unpack OrganizationProperty Data object and return unique UUIDs."
  [src]
  (<- [?id]
      (src _ ?obj)
      (get-organization-id ?obj :> ?id)
      (:distinct true)))

(defn total-occ-by-country-query
  "Count unique occurrence records by country."
  [src]
  (let [uniques (get-unique-occ-by-country src)]
    (<- [?country ?count]
        (uniques ?id ?country)
        (c/count ?count))))

(defn total-occurrences-query
  "Count total unique occurrences."
  [src]
  (let [uniques (get-unique-occurrences src)]
    (<- [?count]
        (uniques ?id)
        (c/count ?count))))

(defn total-publishers-query
  "Count total publishers."
  [src]
  (let [uniques (get-unique-publishers src)]
    (<- [?count]
        (uniques ?id)
        (c/count ?count))))

(defn taxa-count-query
  [src]
  (let [uniques (get-unique-sci-names src)]
    (<- [?count]
        (uniques ?scientific-name)
        (c/count ?count))))

(comment
  ;; records by country
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:records-by-country stats-paths))]
    (??- (total-occ-by-country-query tap)))

  ;; count records
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:total-records stats-paths))]
    (??- (total-occurrences-query tap)))

  ;; total publishers
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:total-publishers stats-paths))]
    (??- (total-publishers-query tap)))

  ;; total taxa
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:total-taxa stats-paths))]
    (??- (taxa-count-query tap)))
  )
