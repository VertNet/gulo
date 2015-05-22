(ns gulo.fields
  "This namespace is the canonical source of fields for Vertnet."
   (:require [gulo.util :as u]))

(def resource-fields
  "Ordered vector of fields used in resource map."
  [:icode :title :citation :contact :dwca :email :eml
   :emlrights :gbifdatasetid :gbifpublisherid :doi :license
   :migrator :networks :orgcountry :orgname :orgstateprovince
   :pubdate :source_url :url])
   
(def base-fields
  "Ordered vector of Darwin Core field names as given by Darwin Core reader output."
  ["id" "associatedmedia" "associatedoccurrences" "associatedorganisms"
   "associatedreferences" "associatedsequences" "associatedtaxa" "bed" "behavior" 
   "catalognumber" "continent" "coordinateprecision" "coordinateuncertaintyinmeters" 
   "country" "countrycode" "county" "dateidentified" "day" "decimallatitude" 
   "decimallongitude" "disposition" "earliestageorloweststage" 
   "earliesteonorlowesteonothem" "earliestepochorlowestseries" 
   "earliesteraorlowesterathem" "earliestperiodorlowestsystem" "enddayofyear" 
   "establishmentmeans" "eventdate" "eventid" "eventremarks" "eventtime" "fieldnotes" 
   "fieldnumber" "footprintspatialfit" "footprintsrs" "footprintwkt" "formation" 
   "geodeticdatum" "geologicalcontextid" "georeferencedby" "georeferenceddate" 
   "georeferenceprotocol" "georeferenceremarks" "georeferencesources" 
   "georeferenceverificationstatus" "group" "habitat" "highergeography" 
   "highergeographyid" "highestbiostratigraphiczone" "identificationid" 
   "identificationqualifier" "identificationreferences" "identificationremarks" 
   "identificationverificationstatus" "identifiedby" "individualcount" "island" 
   "islandgroup" "latestageorhigheststage" "latesteonorhighesteonothem" 
   "latestepochorhighestseries" "latesteraorhighesterathem" "latestperiodorhighestsystem" 
   "lifestage" "lithostratigraphicterms" "locality" "locationaccordingto" "locationid" 
   "locationremarks" "lowestbiostratigraphiczone" "materialsampleid" 
   "maximumdepthinmeters" "maximumdistanceabovesurfaceinmeters" 
   "maximumelevationinmeters" "member" "minimumdepthinmeters" 
   "minimumdistanceabovesurfaceinmeters" "minimumelevationinmeters" "month" 
   "municipality" "occurrenceID" "occurrenceremarks" "occurrencestatus" "organismid" 
   "organismname" "organismremarks" "organismscope" "othercatalognumbers" 
   "pointradiusspatialfit" "preparations" "previousidentifications" "recordedby" 
   "recordnumber" "reproductivecondition" "samplingeffort" "samplingprotocol" "sex" 
   "startdayofyear" "stateprovince" "typestatus" "verbatimcoordinates" 
   "verbatimcoordinatesystem" "verbatimdepth" "verbatimelevation" "verbatimeventdate" 
   "verbatimlatitude" "verbatimlocality" "verbatimlongitude" "verbatimsrs" "waterbody" 
   "year" "type" "modified" "language" "license" "rightsholder" "accessrights" 
   "bibliographiccitation" "references" "institutionid" "collectionid" "datasetid" 
   "institutioncode" "collectioncode" "datasetname" "ownerinstitutioncode" 
   "basisOfRecord" "informationwithheld" "datageneralizations" "dynamicproperties" 
   "taxonid" "scientificnameid" "acceptednameusageid" "parentnameusageid" 
   "originalnameusageid" "nameaccordingtoid" "namepublishedinid" "taxonconceptid" 
   "scientificname" "acceptednameusage" "parentnameusage" "originalnameusage" 
   "nameaccordingto" "namepublishedin" "namepublishedinyear" "higherclassification" 
   "kingdom" "phylum" "class" "order" "family" "genus" "subgenus" "specificepithet" 
   "infraspecificepithet" "taxonrank" "verbatimtaxonrank" "scientificnameauthorship" 
   "vernacularname" "nomenclaturalcode" "taxonomicstatus" "nomenclaturalstatus" 
   "taxonremarks" ])

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
