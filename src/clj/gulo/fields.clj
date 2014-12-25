(ns gulo.fields
  "This namespace is the canonical source of fields for Vertnet."
   (:require [gulo.util :as u]))

(def resource-fields
  "Ordered vector of fields used in resource map."
  [:icode :title :citation :contact :dwca :email :eml
   :emlrights :gbifdatasetid :gbifpublisherid :license
   :migrator :networks :orgcountry :orgname :orgstateprovince
   :pubdate :source_url :url])
   
(def base-fields
  "Ordered vector of VertNet field names. Used as base for various vectors of column 
   and Cascalog field names."
  ["id" "acceptednameusage" "acceptednameusageid" "accessrights" 
   "associatedmedia" "associatedoccurrences" "associatedreferences" "associatedsequences" 
   "associatedtaxa" "basisofrecord" "bed" "behavior" "bibliographiccitation" 
   "catalognumber" "classs" "collectioncode" "collectionid" "continent" 
   "coordinateprecision" "coordinateuncertaintyinmeters" "country" "countrycode" "county" 
   "datageneralizations" "datasetid" "datasetname" "dateidentified" "day" 
   "decimallatitude" "decimallongitude" "disposition" "dynamicproperties" 
   "earliestageorloweststage" "earliesteonorlowesteonothem" 
   "earliestepochorlowestseries" "earliesteraorlowesterathem" 
   "earliestperiodorlowestsystem" "enddayofyear" "establishmentmeans" "eventdate" 
   "eventid" "eventremarks" "eventtime" "family" "fieldnotes" "fieldnumber" 
   "footprintspatialfit" "footprintsrs" "footprintwkt" "formation" "geodeticdatum" 
   "geologicalcontextid" "georeferencedby" "georeferenceddate" "georeferenceprotocol" 
   "georeferenceremarks" "georeferencesources" "georeferenceverificationstatus" "group" 
   "habitat" "higherclassification" "highergeography" "highergeographyid"
   "highestbiostratigraphiczone" "identificationid" "identificationqualifier" 
   "identificationreferences" "identificationremarks" "identificationverificationstatus"
   "identifiedby" "individualcount" "individualid" "informationwithheld" 
   "infraspecificepithet" "institutioncode" "institutionid" "island" "islandgroup" 
   "kingdom" "language" "latestageorhigheststage" "latesteonorhighesteonothem" 
   "latestepochorhighestseries" "latesteraorhighesterathem" "latestperiodorhighestsystem" 
   "lifestage" "lithostratigraphicterms" "locality" "locationaccordingto" "locationid"
   "locationremarks" "lowestbiostratigraphiczone" "materialsampleid" 
   "maximumdepthinmeters" "maximumdistanceabovesurfaceinmeters" "maximumelevationinmeters"
   "member" "minimumdepthinmeters" "minimumdistanceabovesurfaceinmeters" 
   "minimumelevationinmeters" "modified" "month" "municipality" "nameaccordingto"
   "nameaccordingtoid" "namepublishedin" "namepublishedinid" "namepublishedinyear"
   "nomenclaturalcode" "nomenclaturalstatus" "occurrenceid" "occurrenceremarks"
   "occurrencestatus" "order" "originalnameusage" "originalnameusageid" 
   "othercatalognumbers" "ownerinstitutioncode" "parentnameusage" "parentnameusageid" 
   "phylum" "pointradiusspatialfit" "preparations" "previousidentifications" "recordedby" 
   "recordnumber" "references" "reproductivecondition" "rights" "rightsholder" 
   "samplingeffort" "samplingprotocol" "scientificname" "scientificnameauthorship" 
   "scientificnameid" "sex" "specificepithet" "startdayofyear" "stateprovince" "subgenus"
   "taxonconceptid" "taxonid" "taxonomicstatus" "taxonrank" "taxonremarks" "type" 
   "typestatus" "verbatimcoordinates" "verbatimcoordinatesystem" "verbatimdepth"
   "verbatimelevation" "verbatimeventdate" "verbatimlatitude" "verbatimlocality" 
   "verbatimlongitude" "verbatimsrs" "verbatimtaxonrank" "vernacularname" "waterbody" 
   "year" ])

;; Ordered vector of harvesting output column names for use in wide Cascalog sources:
(def harvest-fields
  (concat (vec (map u/kw->field-str resource-fields))
          (map u/str->cascalog-field base-fields)
          ["?dummy"]))

(def harvest-fields-nullable
  (vec (map u/field->nullable harvest-fields)))

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
