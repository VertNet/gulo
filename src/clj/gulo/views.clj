(ns gulo.views
  (:use [cascalog.api]
        [gulo.thrift :as t]
        [gulo.hadoop.pail :as p])
  (:require [cascalog.ops :as c])
  (:import [backtype.hadoop.pail Pail]))

(def stats-paths
  {:total-records ["prop" "RecordProperty" "Occurrence"]
   :records-by-country ["prop" "RecordProperty" "Location"]
   :records-by-collection ["prop" "RecordProperty" "RecordLevel"]
   :records-by-class ["prop" "RecordProperty" "Taxon"]
   :total-taxa ["prop" "RecordProperty" "Taxon"]
   :total-publishers ["prop" "OrganizationProperty"]
   ;;   :downloaded-last-30-days ["prop"]
   ;;   :datasets-last-30-days ["prop"]
   })

(defn unpack-RecordProperty
  "Unpack RecordProperty Data object as far as RecordPropertyValue struct."
  [obj]
  (->> obj .getDataUnit .getFieldValue .getValue .getFieldValue))

(defn get-RecordProperty-id
  "Unpack RecordProperty Data object and return the SourceID."
  [obj]
  (->> obj .getDataUnit .getFieldValue .getId .getFieldValue .getSourceID))

(defn get-ResourceProperty-id
  "Unapck ResourceProperty Data object and return ResourceID."
  [obj]
  (->> obj .getDataUnit .getFieldValue .getId .getFieldValue))

(defn get-DatasetProperty-id
  "Unapck DatasetProperty Data object and return DatasetID."
  [obj]
  (->> obj .getDataUnit .getFieldValue .getId .getFieldValue))

(defn unpack-OrganizationProperty
  "Unpack OrganizationProperty Data object as far as OrganizationPropertyValue."
  [obj]
  (->> obj .getDataUnit .getFieldValue))

(defn get-OrganizationProperty-id
  "Unpack OrganizationProperty Data object and return OrganizationProperty's UUID."
  [obj]
  (->> obj unpack-OrganizationProperty .getId .getUuid))

(defn get-org-id
  "Unpack Data thrift object and return OrganizationProperty's organization id."
  [obj]
  (get-OrganizationProperty-id get-org-id))

(defn get-country
  "Unpack Data thrift object and return RecordProperty's country."
  [obj]
  (.getCountry (unpack-RecordProperty obj)))

(defn get-scientific-name
  "Unpack Data thrift object and return RecordProperty's scientific
  name."
  [obj]
  (.getScientificName (unpack-RecordProperty obj)))

(defn get-collection-code
  "Unpack Data thrift object and return RecordProperty's collection
  code."
  [obj]
  (.getCollectionCode (unpack-RecordProperty obj)))

(defn get-class
  "Unpack Data thrift object and return RecordProperty's taxonomic
  class."
  [obj]
  (.getClazz (unpack-RecordProperty obj)))

(defn get-unique-sci-names
  "Unpack RecordPropertyValue Data objects and return unique
  scientific names."
  [src]
  (<- [?scientific-name]
      (src _ ?obj)
      (get-scientific-name ?obj :> ?scientific-name)
      (:distinct true)))

(defn get-unique-occurrences
  "Unpack RecordProperty Data objects and return unique
  occurrence ids."
  [src]
  (<- [?id]
      (src _ ?obj)
      (get-RecordProperty-id ?obj :> ?id)
      (:distinct true)))

(defn get-unique-occ-by-country
  "Unpack RecordProperty Data objects and return
   unique [?id ?country] tuples."
  [src]
  (<- [?id ?country]
      (src _ ?obj)
      (get-RecordProperty-id ?obj :> ?id)
      (get-country ?obj :> ?country)
      (:distinct true)))

(defn get-unique-publishers
  "Unpack OrganizationProperty Data object and return unique UUIDs."
  [src]
  (<- [?id]
      (src _ ?obj)
      (get-org-id ?obj :> ?id)
      (:distinct true)))

(defn get-unique-by-coll-code
  "Unpack RecordProperty Data object and return unique collection-code
  and id tuples."
  [src]
  (<- [?coll-code ?id]
      (src _ ?obj)
      (get-collection-code ?obj :> ?coll-code)
      (get-RecordProperty-id ?obj :> ?id)
      (:distinct true)))

(defn get-unique-by-occ-class
  "Unpack ReordProperty Data object and return unique id and class
  tuples."
  [src]
  (<- [?id ?class]
      (src _ ?obj)
      (get-class ?obj :> ?class)
      (get-RecordProperty-id ?obj :> ?id)
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
  "Count taxa."
  [src]
  (let [uniques (get-unique-sci-names src)]
    (<- [?count]
        (uniques ?scientific-name)
        (c/count ?count))))

(defn total-by-collection-query
  "Count unique records by collection."
  [src]
  (let [uniques (get-unique-by-coll-code src)]
    (<- [?coll-code ?count]
        (uniques ?coll-code ?id)
        (c/count ?count))))

(defn total-by-class-query
  "Count unique records by class."
  [src]
  (let [uniques (get-unique-by-occ-class src)]
    (<- [?class ?count]
        (uniques ?id ?class)
        (c/count ?count))))
