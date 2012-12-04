(ns gulo.thrift
  (:use [dwca.core :as dwca])
  (:require [clojure.java.io :as io]
            [clojure.string :as s])
  (:import [gulo.schema
            Data DataUnit DatasetID DatasetProperty DatasetPropertyValue
            DatasetRecordEdge Event GeologicalContext Identification Location
            MeasurementOrFact Occurrence OragnizationPropertyValue
            OrganizationDatasetEdge OrganizationID OrganizationProperty Pedigree
            RecordID RecordProperty RecordPropertyValue ResourceRelationship
            Taxon]
           [org.apache.thrift TBase TUnion]
           [clojure.lang Reflector]))

(defn construct
  [type keys fields]
  (Reflector/invokeConstructor type (to-array (map #(% fields) keys))))

(defn construct-union
  [type property fields]  
  (let [key (keyword (s/lower-case property))
        value (key fields)]
    (if value
      (Reflector/invokeStaticMethod type property (into-array Object [value])))))

(def RecordID-
  (let [type RecordID
        keys [:id]
        property "id"]
    (partial construct-union type property)))

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

(def RecordPropertyValue-
  (let [type RecordPropertyValue
        f (fn [type property fields]
            (let [key (keyword (s/lower-case property))
                  value (key fields)]
              (if value
                (Reflector/invokeStaticMethod type property (into-array Object [value])))))]
    (partial construct-union type)))

(defn record-property-values
  [fields]
  "Return vector of RecordProperty objects for all values in supplied fields map."
  (let [names {:accessrights "accessRights" :basisofrecord "basisOfRecord"
              :bibliographiccitation "bibliographicCitation"
              :collectioncode "collectionCode" :collectionid "collectionID"
              :datageneralizations "dataGeneralizations" :datasetid "datasetID"
              :datasetname "datasetName" :dynamicproperties "dynamicProperties"
              :informationwithheld "informationWithheld"
              :institutioncode "institutionCode" :institutionid "institutionID"
              :language "language" :modified "modified"
              :ownerinstitutioncode "ownerInstitutionCode"
              :references "references" :string "string"
              :rightsholder "rightsHolder" :type "type" :occurrence "occurrence"
              :event "event" :location "location"
              :geologicalcontext "geologicalContext"
              :identification "identification" :taxon "taxon"
              :resourcerelationship "resourceRelationship"
              :measurementorfact "measurementOrFact"}
        objects {:occurrence (Occurrence- fields)
                 :event (Event- fields)
                 :location (Location- fields)
                 :geologicalcontext (GeologicalContext- fields)
                 :identification (Identification- fields)
                 :taxon (Taxon- fields)
                 :resourcerelationship (ResourceRelationship- fields)
                 :measurementorfact (MeasurementOrFact- fields)}
        fields (merge fields objects)]
    (filter #(not= % nil)
            (map #(RecordPropertyValue- (% names) fields) (keys names)))))

(comment
  (def path (.getPath (io/resource "archive-occ")))
  (def recs (dwca/get-records path))
  (def rec (first recs)))
