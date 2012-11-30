(ns gulo.thrift
  (:import [gulo.schema
            Data DataUnit Event GeologicalContext Identification Location
            MeasurementOrFact Occurrence Pedigree RecordID RecordProperty
            RecordPropertyValue ResourceRelationship Taxon]
           [org.apache.thrift TBase TUnion]))

(defn RecordID*
  [source-id dataset-uuid]
  (RecordID. source-id dataset-uuid))

(defn RecordPropertyValue*
  [access-rights]
  (let [obj (RecordPropertyValue.)]
    (if access-rights
      (doto obj
        (.setAccessRights access-rights)))
    obj))

(defn RecordProperty*
  [id property]
  (RecordProperty. id property))

(defn Pedigree*
  [secs]
  (Pedigree. secs))

(defn Data*
  [id value pedigree]
  (let [property (RecordProperty. id value)
        unit (->> property DataUnit/recordProperty)
        data (Data. pedigree unit)]
    data))
