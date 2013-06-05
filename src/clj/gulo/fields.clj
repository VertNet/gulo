(ns gulo.fields
  "This namespace is the canonical source of fields for Vertnet."
   (:require [gulo.util :as u]))

(def resource-fields
  "Ordered vector of fields used in resource map."
  [:pubdate :url :eml :dwca :title :icode :description :contact :orgname :email
   :emlrights :count :citation])

(def base-fields
  "Ordered vector of VertNet field names. Used as base for various
   vectors of column and Cascalog field names."
  ["harvestid" "id" "associatedmedia" "associatedoccurrences" "associatedreferences"
   "associatedsequences" "associatedtaxa" "basisofrecord" "bed" "behavior"
   "catalognumber" "collectioncode" "collectionid" "continent"
   "coordinateprecision" "coordinateuncertaintyinmeters" "country" "countrycode"
   "county" "datageneralizations" "dateidentified" "day" "decimallatitude"
   "decimallongitude" "disposition" "earliestageorloweststage"
   "earliesteonorlowesteonothem" "earliestepochorlowestseries"
   "earliesteraorlowesterathem" "earliestperiodorlowestsystem" "enddayofyear"
   "establishmentmeans" "eventattributes" "eventdate" "eventid" "eventremarks"
   "eventtime" "fieldnotes" "fieldnumber" "footprintspatialfit" "footprintwkt"
   "formation" "geodeticdatum" "geologicalcontextid" "georeferenceprotocol"
   "georeferenceremarks" "georeferencesources" "georeferenceverificationstatus"
   "georeferencedby" "group" "habitat" "highergeography" "highergeographyid"
   "highestbiostratigraphiczone" "identificationattributes" "identificationid"
   "identificationqualifier" "identificationreferences" "identificationremarks"
   "identifiedby" "individualcount" "individualid" "informationwithheld"
   "institutioncode" "island" "islandgroup" "latestageorhigheststage"
   "latesteonorhighesteonothem" "latestepochorhighestseries"
   "latesteraorhighesterathem" "latestperiodorhighestsystem" "lifestage"
   "lithostratigraphicterms" "locality" "locationattributes" "locationid"
   "locationremarks" "lowestbiostratigraphiczone" "maximumdepthinmeters"
   "maximumdistanceabovesurfaceinmeters" "maximumelevationinmeters"
   "measurementaccuracy" "measurementdeterminedby" "measurementdetermineddate"
   "measurementid" "measurementmethod" "measurementremarks" "measurementtype"
   "measurementunit" "measurementvalue" "member" "minimumdepthinmeters"
   "minimumdistanceabovesurfaceinmeters" "minimumelevationinmeters" "month"
   "occurrenceattributes" "occurrencedetails" "occurrenceid" "occurrenceremarks"
   "othercatalognumbers" "pointradiusspatialfit" "preparations"
   "previousidentifications" "recordnumber" "recordedby" "relatedresourceid"
   "relationshipaccordingto" "relationshipestablisheddate"
   "relationshipofresource" "relationshipremarks" "reproductivecondition"
   "resourceid" "resourcerelationshipid" "samplingprotocol" "sex"
   "startdayofyear" "stateprovince" "taxonattributes" "typestatus"
   "verbatimcoordinatesystem" "verbatimcoordinates" "verbatimdepth"
   "verbatimelevation" "verbatimeventdate" "verbatimlatitude"
   "verbatimlocality" "verbatimlongitude" "waterbody" "year" "footprintsrs"
   "georeferenceddate" "identificationverificationstatus" "institutionid"
   "locationaccordingto" "municipality" "occurrencestatus"
   "ownerinstitutioncode" "samplingeffort" "verbatimsrs" "locationaccordingto7"
   "taxonid" "taxonconceptid" "datasetid" "datasetname" "source" "modified"
   "accessrights" "rights" "rightsholder" "language" "higherclassification"
   "kingdom" "phylum" "classs" "order" "family" "genus" "subgenus"
   "specificepithet" "infraspecificepithet" "scientificname" "scientificnameid"
   "vernacularname" "taxonrank" "verbatimtaxonrank" "infraspecificmarker"
   "scientificnameauthorship" "nomenclaturalcode" "namepublishedin"
   "namepublishedinid" "taxonomicstatus" "nomenclaturalstatus" "nameaccordingto"
   "nameaccordingtoid" "parentnameusageid" "parentnameusage"
   "originalnameusageid" "originalnameusage" "acceptednameusageid"
   "acceptednameusage" "taxonremarks" "dynamicproperties" "namepublishedinyear"])

;; Ordered vector of harvesting output column names for use in wide Cascalog sources:
(def harvest-fields
  (concat (vec (map u/kw->field-str resource-fields))
          (map u/str->cascalog-field base-fields)
          ["?dummy"]))

(def occ-fields
  (into ["?occ-uuid"] harvest-fields))

;; Ordered vector of occ table column names for use in wide Cascalog sources:
(def rec-fields
  (concat ["?occ-id"] (map u/kw->field-str base-fields) ["?iname" "?icode"]))

;; Ordered vector of column names for the occ table.
(def vertnet-columns
  (concat ["taxloc_uuid" "occ_uuid"] (u/handle-sql-reserved base-fields)
          ["iname" "icode"]))

(def vertnet-fields
  (concat ["?taxloc-uuid" "?tax-uuid" "?loc-uuid" "?occ-uuid"] harvest-fields))

(def harvest-tax-loc-fields
  ["?tax-uuid" "?loc-uuid" "?occ-uuid" "?scientificname" "?kingdom"
   "?phylum" "?classs" "?order" "?family" "?genus" "?lat" "?lon"])
