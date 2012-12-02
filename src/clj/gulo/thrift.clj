(ns gulo.thrift
  (:use [dwca.core :as dwca])
  (:require [clojure.java.io :as io])
  (:import [gulo.schema
            Data DataSet DataUnit Event GeologicalContext Identification
            Location MeasurementOrFact Metadata Occurrence Pedigree RecordID
            RecordProperty RecordPropertyValue ResourceRelationship Taxon]
           [org.apache.thrift TBase TUnion]
           [clojure.lang Reflector]))

(defn construct
  [type keys fields]
  (Reflector/invokeConstructor type (to-array (map #(% fields) keys))))

(def Occurrence-
  (let [type Occurrence
        keys [:associatedmedia :associatedoccurrences :associatedreferences
              :associatedsequences :associatedtaxa :behavior :catalognumber
              :disposition :establishmentmeans :individualcount :individualid
              :lifestage :occurrenceid :occurrenceremarks :occurrencestatus
              :othercatalognumbers :preparations :previousidentifications
              :recordnumber :recordedby :reproductivecondition :sex]]
    (partial construct type keys)))

(def Event-
  (let [type Event
        keys [:day :enddayofyear :eventdate :eventid :eventremarks :eventtime
              :fieldnotes :fieldnumber :habitat :month :samplingeffort
              :samplingprotocol :startdayofyear :verbatimeventdate :year]]
    (partial construct type keys)))

(def Location-
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
    (partial construct type keys)))

(def GeologicalContext-
  (let [type GeologicalContext
        keys [:bed :earliestageorloweststage :earliesteonorlowesteonothem
              :earliestepochorlowestseries :earliesteraorlowesterathem
              :earliestperiodorlowestsystem :formation :geologicalcontextid
              :group :highestbiostratigraphiczone :latestageorhigheststage
              :latesteonorhighesteonothem :latestepochorhighestseries
              :latesteraorhighesterathem :latestperiodorhighestsystem
              :lithostratigraphicterms :lowestbiostratigraphiczone :member]]
    (partial construct type keys)))

(def Identification-
  (let [type Identification
        keys [:identificationid :identifiedby :dateidentified
              :identificationreferences :identificationverificationstatus
              :identificationremarks :identificationqualifier :typestatus]]
    (partial construct type keys)))

(def Taxon-
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
    (partial construct type keys)))

(def ResourceRelationship-
  (let [type ResourceRelationship
        keys [:relatedresourceid :relationshipaccordingto
              :relationshipestablisheddate :relationshipofresource
              :relationshipremarks :resourceid :resourcerelationshipid]]
    (partial construct type keys)))

(def MeasurementOrFact-
  (let [type MeasurementOrFact
        keys [:meaasurementaccuracy :measurementdeterminedby
              :measurementdetermineddate :measurementid :measurementmethod
              :measurementremarks :measurementtype :measurementunit
              :measurementvalue]]
    (partial construct type keys)))

; RPV is a union. Check schema to make sure general for other data
; (not just dwc records).
(def RecordPropertyValues-
  (let [type RecordPropertyValue
        keys [:accessrights :basisofrecord :bibliographiccitation :collectioncode
              :collectionid :datageneralizations :datasetid :datasetname
              :dynamicproperties :informationwithheld :institutioncode
              :institutionid :language :modified :ownerinstitutioncode
              :references :rights :rightsholder :type :occurrence :event
              :location :geologicalcontext :identification :taxon
              :resourcerelationship :measurementorfact]
        f (fn [type keys fields]
            (let [new-fields {:occurrence (Occurrence- fields)
                              :event (Event- fields)
                              :location (Location- fields)
                              :geologicalcontext (GeologicalContext- fields)
                              :identification (Identification- fields)
                              :taxon (Taxon- fields)
                              :resourcerelationship (ResourceRelationship- fields)
                              :measurementorfact (MeasurementOrFact- fields)}
                  fields (merge new-fields fields)]
              (construct type keys fields)))]
    (partial f type keys)))

(defn RecordID*
  [source-id dataset-uuid]
  (RecordID. source-id dataset-uuid))



;; (defn Occurrence*
;;   [props]
;;   (let [keys [:associatedmedia :associatedoccurrences :associatedreferences
;;               :associatedsequences :associatedtaxa :behavior :catalognumber
;;               :disposition :establishmentmeans :individualcount :individualid
;;               :lifestage :occurrenceid :occurrenceremarks :occurrencestatus
;;               :othercatalognumbers :preparations :previousidentifications
;;               :recordnumber :recordedby :reproductivecondition :sex]
;;         vals (to-array (map #(% props) keys))]
;;     (Reflector/invokeConstructor Occurrence vals)))

(defn RecordPropertyValue-
  [props]
  (let [{:keys [f-name m-name l-name]} props]))

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

(defn DataSet*
  [abstract uuid rights lang orgname date title]
  (DataSet. abstract uuid rights lang orgname date title))

(defn Metadata*
  [dataset]
  (Metadata. dataset))

(defn Pedigree*
  [secs metadata]
  (Pedigree. secs metadata))

(defn Data*
  [id value pedigree]
  (let [property (RecordProperty. id value)
        unit (->> property DataUnit/recordProperty)
        data (Data. pedigree unit)]
    data))
