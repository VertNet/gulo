(ns gulo.hadoop.pail
  (:use cascalog.api
        [cascalog.io :only (with-fs-tmp)]
        [gulo.thrift :as thrift])
  (:require [clojure.string :as s])
  (:import [java.util List]
           [gulo.schema
            Data DataUnit DatasetID DatasetProperty DatasetPropertyValue
            DatasetRecordEdge Event GeologicalContext Identification Location
            MeasurementOrFact Occurrence OrganizationPropertyValue
            OrganizationID OrganizationProperty Pedigree
            RecordID RecordLevel RecordProperty RecordPropertyValue RecordSource
            ResourceID ResourceDatasetEdge ResourceOrganizationEdge
            ResourcePropertyValue ResourceProperty ResourceRelationship Taxon]
           [backtype.cascading.tap PailTap PailTap$PailTapOptions]
           [backtype.hadoop.pail PailStructure Pail]
           [gulo.tap ThriftPailStructure]))

(gen-class :name gulo.hadoop.pail.DataPailStructure
           :extends gulo.tap.ThriftPailStructure
           :prefix "pail-")

(defn pail-getType [this] Data)
(defn pail-createThriftObject [this] (Data.))

(gen-class :name gulo.hadoop.pail.SplitDataPailStructure
           :extends gulo.hadoop.pail.DataPailStructure
           :prefix "split-")

(defn slugify
  [str]
  "Return supplied string lower case with whitespace replaced with dash."
  (let [clean (s/lower-case (s/trim str))]
    (s/replace clean #"\s+" "-")))

(defmulti property-target class)

(defmethod property-target ResourceProperty
  [x]
  ["prop" "ResourceProperty"])

(defmethod property-target DatasetProperty
  [x]
  ["prop" "DatasetProperty"])

(defmethod property-target OrganizationProperty
  [x]
  ["prop" "OrganizationProperty"])

(defmethod property-target RecordProperty
  [x]
  (let [prop-value (.getValue x)
        value (-> prop-value .getFieldValue)
        name (last (clojure.string/split (str (class value)) #"\."))]
    ["prop" "RecordProperty" name]))

(defn split-getTarget
  [this ^Data d]
  "museum-of-vertebrate-zoology/nmmnh-mammal-uuid"
  (let [prop (-> d .getDataUnit .getFieldValue)
        target (property-target prop)]
    target))

(defn split-isValidTarget [this dirs]
  (let [dirs-set (set dirs)]
    (boolean
     (if (contains? dirs-set "RecordProperty")
       (#{3 4} (count dirs-set))
       (#{2 3} (count dirs-set))))))

(defn pail-structure []
  (gulo.hadoop.pail.SplitDataPailStructure.))

(defn- pail-tap
  [path colls structure]
  (let [seqs (into-array List colls)
        spec (PailTap/makeSpec nil structure)
        opts (PailTap$PailTapOptions. spec "!data" seqs nil)]
    (PailTap. path opts)))

(defn split-chunk-tap [path & colls]
  (pail-tap path colls (pail-structure)))

;; TODO: If the pail doesn't exist, rather than providing
;; pail-structure, pull the structure information out of the tap.

(defn ?pail-*
  "Executes the supplied query into the DataChunkPailStructure pail
  located at the supplied path, consolidating when finished."
  [tap pail-path query]
  (let [pail (Pail/create pail-path (pail-structure) false)]
    (with-fs-tmp [_ tmp]
      (?- (tap tmp) query)
      (.absorb pail (Pail. tmp)))))

;; TODO: This makes the assumption that the pail-tap is being created
;; in the macro call. Fix this by swapping the temporary path into the
;; actual tap vs destructuring.

(defmacro ?pail-
  "Executes the supplied query into the pail located at the supplied
  path, consolidating when finished."
  [[tap path] query]
  (list `?pail-* tap path query))

(defn to-pail
  "Executes the supplied `query` into the pail at `pail-path`. This
  pail must make use of the `DataChunkPailStructure`."
  [pail-path query]
  (?pail- (split-chunk-tap pail-path)
          query))

(defmain consolidate [pail-path]
  (.consolidate (Pail. pail-path)))

(defmain absorb [from-pail to-pail]
  (.absorb (Pail. to-pail)
           (Pail. from-pail)))


(comment
  (let [id (thrift/RecordID* "123" "343fs33")
        value (thrift/RecordPropertyValue* "mine!")
        dataset (thrift/DataSet* "abs" "uuid" "rights" "lang"
                                 "Museum of Vertebrate Zoology" "date"
                                 "MVZ Herps")
        metadata (thrift/Metadata* dataset)
        pedigree (thrift/Pedigree* 23 metadata)
        data (thrift/Data* id value pedigree)
        a [[data]]
        q (<- [?a]
            (a ?a))]
  (to-pail "/tmp/gulo" q)))
