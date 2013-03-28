(ns gulo.views
  (:use [cascalog.api]
        [gulo.thrift :as t]
        [gulo.hadoop.pail :as p])
  (:require [cascalog.ops :as c])
  (:import [backtype.hadoop.pail Pail])
  (:import [gulo.schema
            Data DataUnit DatasetID DatasetProperty DatasetPropertyValue
            DatasetRecordEdge Event GeologicalContext Identification Location
            MeasurementOrFact Occurrence OrganizationPropertyValue
            OrganizationID OrganizationProperty Pedigree RecordID RecordLevel
            RecordProperty RecordPropertyValue RecordSource ResourceID
            ResourceDatasetEdge ResourceOrganizationEdge
            ResourcePropertyValue ResourceProperty ResourceRelationship Taxon]))

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
  (.getCountry (t/unpack-RecordProperty obj)))

(defn get-scientific-name
  "Unpack Data thrift object and return RecordProperty's scientific
  name."
  [obj]
  (.getScientificName (t/unpack-RecordProperty obj)))

(defn get-collection-code
  "Unpack Data thrift object and return RecordProperty's collection
  code."
  [obj]
  (.getCollectionCode (t/unpack-RecordProperty obj)))

(defn get-class
  "Unpack Data thrift object and return RecordProperty's taxonomic
  class."
  [obj]
  (.getClazz (t/unpack-RecordProperty obj)))

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

(defn get-most-recent-fields
  "Return the unpacked fields from the most recent RecordProperty object."
  [tuples]
  (let [max-time (apply max (map first tuples))
        f #(= max-time (first %))
        newest (last (first (filter f tuples)))
        rec-prop-val (t/unpack-RecordProperty newest)]
    [(t/unpack* rec-prop-val)]))

(defbufferop wrap-get-most-recent-fields
  "Wrapper for `get-most-recent fields`"
  [tuples]
  (get-most-recent-fields tuples))

(defn keep-most-recent
  "Query unpacks Data objects, determines which object of each
   RecordProperty class is most recent, and returns that object's
   RecordProperty fields."
  [src]
  (<- [?id ?class-str ?fields]
      (src _ ?data)
      (t/unpack ?data :> ?pedigree _)
      (get-RecordProperty-id ?data :> ?id)
      (t/unpack ?pedigree :> ?time)
      (wrap-get-most-recent-fields ?time ?data :> ?fields)
      (unpack-RecordProperty ?data :> ?prop)
      (class ?prop :> ?class)
      (str ?class :> ?class-str)))

(defn concat-fields
  "Flattens multiple tuples into a single vector. Intended for use with
   tuples coming in from a `defbufferop`.

   Usage:
     (concat-fields [[1 2 3] [4 5 6]])
     ;=> [[[1 2 3 4 5 6]]]"
  [tuples]
  [[(vec (flatten tuples))]])

(defbufferop wrap-concat-fields
  "Wraps `concat-fields`."
  [tuples]
  (concat-fields tuples))

(defn concat-fields-query
  "Concatenate fields from various RecordProperty Data objects for each
   record (based on record id)."
  [pail-path dirs]
  (let [src (p/split-chunk-tap pail-path dirs)
        newest-src (keep-most-recent src)]
    (<- [?id ?all-fields]
        (newest-src ?id ?class ?fields)
        (wrap-concat-fields ?fields :> ?all-fields))))
