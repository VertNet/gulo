(ns gulo.thrift
  (:use [dwca.core :as dwca])
  (:require [clojure.java.io :as io]
            [clojure.string :as s])
  (:import [gulo.schema
            Data DataUnit DatasetID DatasetProperty DatasetPropertyValue
            DatasetRecordEdge Event GeologicalContext Identification Location
            MeasurementOrFact Occurrence OragnizationPropertyValue
            OrganizationDatasetEdge OrganizationID OrganizationProperty Pedigree
            RecordID RecordLevel RecordProperty RecordPropertyValue RecordSource
            ResourceID ResourceDatasetEdge ResourceOrganizationEdge
            ResourcePropertyValue ResourceProperty ResourceRelationship Taxon]
           [org.apache.thrift TBase TUnion]
           [clojure.lang Reflector]))

(defn box-int
  [x]
  "Clojure 1.3 automatically coerces primitive ints to longs. For Thrift, we need
  ints, so this helper function boxes an integer value to avoid that coercion.
  An upgrade to Clojure 1.4 lets us use (integer) function instead."
  (Integer. (.intValue x)))

(defn epoch  
  []
  "Return seconds since epoch."
  (int (/ (System/currentTimeMillis) 1000)))

(defn create
  [type keys fields]
  "Create a Thrift struct of supplied type using keys and fields map via reflection."
  (Reflector/invokeConstructor type (to-array (map #(% fields) keys))))

(defn create-union
  [type property value]
  "Create Thrift union of supplied type using keys and fields map via reflection."
  (if value
    (Reflector/invokeStaticMethod type property (into-array Object [value]))))

(defn create-union-val
  [type key value]
  (let [property (name key)]
    (create-union type property value)))

(defn create-struct-val
  [type key value]
  (let [keys [(name key)]
        fields {(s/lower-case key) value}]
    (create type keys fields)))

;; Multimethods for creating DataUnit unions based on class:
(defmulti create-dataunit class)

(defmethod create-dataunit RecordProperty
  [x]
  (DataUnit/recordProperty x))

(defmethod create-dataunit DatasetProperty
  [x]
  (DataUnit/datasetProperty x))

(defmethod create-dataunit ResourceProperty
  [x]
  (DataUnit/resourceProperty x))

(defmethod create-dataunit OrganizationProperty
  [x]
  (DataUnit/organizationProperty x))

(defmethod create-dataunit DatasetRecordEdge
  [x]
  (DataUnit/datasetRecord x))

(defmethod create-dataunit ResourceDatasetEdge
  [x]
  (DataUnit/resourceDataset x))

(defmethod create-dataunit ResourceOrganizationEdge
  [x]
  (DataUnit/resourceOrganization x))

;; Multimethods for creating RecordPropertyValue unions based on class:
(defmulti create-rec-prop-val class)

(defmethod create-rec-prop-val RecordLevel
  [x]
  (RecordPropertyValue/recordLevel x))

(defmethod create-rec-prop-val Occurrence
  [x]
  (RecordPropertyValue/occurrence x))

(defmethod create-rec-prop-val Event
  [x]
  (RecordPropertyValue/event x))

(defmethod create-rec-prop-val Location
  [x]
  (RecordPropertyValue/location x))

(defmethod create-rec-prop-val GeologicalContext
  [x]
  (RecordPropertyValue/geologicalContext x))

(defmethod create-rec-prop-val Identification
  [x]
  (RecordPropertyValue/identification x))

(defmethod create-rec-prop-val Taxon
  [x]
  (RecordPropertyValue/taxon x))

(defmethod create-rec-prop-val ResourceRelationship
  [x]
  (RecordPropertyValue/resourceRelationship x))

(defmethod create-rec-prop-val MeasurementOrFact
  [x]
  (RecordPropertyValue/measurementOrFact x))

;; Multimethods for creating RecordID union based on class:
(defmulti create-rec-id class)

(defmethod create-rec-id String
  [x]
  (RecordID/guid x))

(defmethod create-rec-id RecordSource
  [x]
  (RecordID/recordSource x))

;; Multimethods for creating ResourceID based on class:
(defmulti create-resource-id class)

(defmethod create-resource-id String
  [x]
  (ResourceID/uuid x))

(defn RecordSource-  
  [source-id dataset-uuid]
  "Create RecordSource Thrift object from supplied sourceID and datasetUUID."
  (RecordSource. source-id dataset-uuid))

(def RecordID-
  "Create RecordID Thrift object by specifying a property name as defined in the
   Thift schema definition as a keyword. For example, to create a RecordID that
   represents a guid:

    > (RecordID- :guid (gen-uuid))
  "
  (let [type RecordID]
    (partial create-union-val type)))


(def Occurrence-
  "Create an Occurrence Thrift object from supplied map of Darwin Core record
   fields."
  (let [type Occurrence
        keys [:associatedmedia :associatedoccurrences :associatedreferences
              :associatedsequences :associatedtaxa :behavior :catalognumber
              :disposition :establishmentmeans :individualcount :individualid
              :lifestage :occurrenceid :occurrenceremarks :occurrencestatus
              :othercatalognumbers :preparations :previousidentifications
              :recordnumber :recordedby :reproductivecondition :sex]]
    (partial create type keys)))

(def Event-
  "Create an Event Thrift object from supplied map of Darwin Core record
   fields."
  (let [type Event
        keys [:day :enddayofyear :eventdate :eventid :eventremarks :eventtime
              :fieldnotes :fieldnumber :habitat :month :samplingeffort
              :samplingprotocol :startdayofyear :verbatimeventdate :year]]
    (partial create type keys)))

(def Location-
  "Create a Location Thrift object from supplied map of Darwin Core record
   fields."
  (let [type Location
        keys [:continent :coordinateprecision :coordinateuncertaintyinmeters
              :country :countrycode :county :decimallatitude :decimallongitude
              :footprintsrs :footprintspatialfit :footprintwkt :geodeticdatum
              :georeferenceprotocol :georeferenceremarks :georeferencesources
              :georeferenceverificationstatus :georeferencedby :georeferenceddate
              :highergeography :highergeographyid :island :islandgroup :locality
              :locationaccordingto :locationid :locationremarks
              :maximumdepthinmeters :maximumdistanceabovesurfaceinmeters
              :maximumelevationinmeters :minimumdepthinmeters
              :minimumdistanceabovesurfaceinmeters :minimumelevationinmeters
              :municipality :pointradiusspatialfit :stateprovince
              :verbatimcoordinatesystem :verbatimcoordinates :verbatimdepth
              :verbatimelevation :verbatimlatitude :verbatimlocality
              :verbatimlongitude :verbatimsrs :waterbody]]
    (partial create type keys)))

(def GeologicalContext-
  "Create a GeologicalContext Thrift object from supplied map of Darwin Core
   record fields."
  (let [type GeologicalContext
        keys [:bed :earliestageorloweststage :earliesteonorlowesteonothem
              :earliestepochorlowestseries :earliesteraorlowesterathem
              :earliestperiodorlowestsystem :formation :geologicalcontextid
              :group :highestbiostratigraphiczone :latestageorhigheststage
              :latesteonorhighesteonothem :latestepochorhighestseries
              :latesteraorhighesterathem :latestperiodorhighestsystem
              :lithostratigraphicterms :lowestbiostratigraphiczone :member]]
    (partial create type keys)))

(def Identification-
  "Create an Identification Thrift object from supplied map of Darwin Core
   record fields."  
  (let [type Identification
        keys [:identificationid :identifiedby :dateidentified
              :identificationreferences :identificationverificationstatus
              :identificationremarks :identificationqualifier :typestatus]]
    (partial create type keys)))

(def Taxon-
  "Create a Taxon Thrift object from supplied map of Darwin Core record fields."
  (let [type Taxon
        keys [:acceptednameusage :acceptednameusageid :clazz :family :genus
              :higherclassification :infraspecificepithet :kingdom
              :nameaccordingto :nameaccordingtoid :namepublishedin
              :namepublishedinid :namepublishedinyear :nomenclaturalcode
              :nomenclaturalstatus :order :originalnameusage :originalnameusageid
              :parentnameusage :parentnameusageid :phylum :scientificname
              :scientificnameauthorship :scientificnameid :specificepithet
              :subgenus :taxonconceptid :taxonid :taxonrank :taxonremarks
              :taxonomicstatus :verbatimtaxonrank :vernacularname]]
    (partial create type keys)))

(def ResourceRelationship-
  "Create a ResourceRelationship Thrift object from supplied map of Darwin Core
   record fields."  
  (let [type ResourceRelationship
        keys [:relatedresourceid :relationshipaccordingto
              :relationshipestablisheddate :relationshipofresource
              :relationshipremarks :resourceid :resourcerelationshipid]]
    (partial create type keys)))

(def MeasurementOrFact-
  "Create a MeasurementOrFact Thrift object from supplied map of Darwin Core
   record fields."  
  (let [type MeasurementOrFact
        keys [:meaasurementaccuracy :measurementdeterminedby
              :measurementdetermineddate :measurementid :measurementmethod
              :measurementremarks :measurementtype :measurementunit
              :measurementvalue]]
    (partial create type keys)))

(def RecordLevel-
  "Create a RecordLevel Thrift object from supplied map of Darwin Core record
   fields."  
  (let [type RecordLevel
        keys [:accessrights :basisofrecord :bibliographiccitation :collectioncode
              :collectionid :datageneralizations :datasetid :datasetname
              :dynamicproperties :informationwithheld :institutioncode
              :institutionid :language :modified :ownerinstitutioncode
              :references :rights :rightsholder :type]]
    (partial create type keys)))

(def DatasetID-
  "(DatasetID- :url foo)"
  (let [type DatasetID]
    (partial create-union-val type)))

(def RecordPropertyValue-
  "Create a RecordPropertyValue Thrift object from supplied property name and
   map of Darwin Core field values."
  (let [type RecordPropertyValue]
    (partial create-union type)))

(defn record-property-values
  [fields]
  "Return vector of RecordPropertyValue Thrift objects from supplied map of
   Darwin Core record fields."
  (let [objects [(Occurrence- fields)
                 (Event- fields)
                 (Location- fields)
                 (GeologicalContext- fields)
                 (Identification- fields)
                 (Taxon- fields)
                 (ResourceRelationship- fields)
                 (MeasurementOrFact- fields)
                 (RecordLevel- fields)]]
    (filter #(not= % nil)
            (map #(create-rec-prop-val %) objects))))

(defn resource-property-values
  [resource]  
  (for [[k v] resource]
    (create-union ResourcePropertyValue (name k) v)))

(defn ResourceProperty-
  [id value]
  (ResourceProperty. id value))

(defn resource-properties
  [id resource]
  (map #(ResourceProperty- id %) (resource-property-values resource)))

(defn RecordProperty-
  [id value]
  "Create RecordProperty Thrift object from supplied RecordID and
   RecordPropertyValue."
  (RecordProperty. id value))

(defn record-properties
  [id fields]
  "Return vector of RecordProperty Thrift objects from supplied RecordID and
   map of Darwin Core record fields."
  (map #(RecordProperty- id %) (record-property-values fields)))

(defn Pedigree-
  []
  (Pedigree. (epoch)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; API

(defn resource-data
  "Return generator of Data Thrift objects each representing a
   ResourceDataProperty created from the supplied resource property value map."
  [resource]
  (let [id (create-resource-id (:url resource))
        props (resource-properties id resource)
        data (for [p props]
               (Data. (Pedigree. (epoch)) (create-dataunit p)))]
    (vec (map vector data))))

(comment
  (def path (.getPath (io/resource "archive-occ")))
  (def recs (dwca/get-records path))
  (def rec (first recs)))
