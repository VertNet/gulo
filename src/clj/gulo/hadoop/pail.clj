(ns gulo.hadoop.pail
  (:use cascalog.api
        [cascalog.io :only (with-fs-tmp)]
        [gulo.thrift :as thrift])
  (:import [java.util List]
           [gulo.schema
            Data DataUnit Event GeologicalContext Identification Location
            MeasurementOrFact Occurrence Pedigree RecordID RecordProperty
            RecordPropertyValue ResourceRelationship Taxon]
           [backtype.cascading.tap PailTap PailTap$PailTapOptions]
           [backtype.hadoop.pail PailStructure Pail]
           [gulo.tap ThriftPailStructure]))

(gen-class :name gulo.hadoop.pail.DataPailStructure
           :extends gulo.tap.ThriftPailStructure
           :prefix "pail-")

(defn pail-getType [this] Data)
(defn pail-createThriftObject [this] (Data.))

; (gen-class :name gulo.hadoop.pail.SplitDataPailStructure
;            :extends gulo.hadoop.pail.DataPailStructure
;            :prefix "split-")

; (defn split-getTarget [this ^Chunk d]
;   (let [location   (-> d .getLocationProperty .getProperty .getFieldValue)
;         resolution (format "%s-%s"
;                            (.getResolution location)
;                            (.getTemporalRes d))]
;     [(.getDataset d) resolution]))

(defn split-isValidTarget [this dirs]
  (boolean (#{2 3} (count dirs))))

(defn pail-structure []
  (gulo.hadoop.pail.DataPailStructure.))

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
        pedigree (thrift/Pedigree* 23)
        data (thrift/Data* id value pedigree)
        a [[data]]
        q (<- [?a]
            (a ?a))]
  (to-pail "/tmp/test" q)))
