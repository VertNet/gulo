(ns gulo.util
  "This namespace contains utility functions.")

(defn gen-uuid
  "Return a randomly generated UUID string."
  [& x] ;; Cascalog ArityException: Wrong number of args without [& x]
  (str (java.util.UUID/randomUUID)))

;; Valid ranges for latitude and longitude.
(def latlon-range {:lat-min -90 :lat-max 90 :lon-min -180 :lon-max 180})

(defn read-latlon
  "Converts lat and lon values from string to number."
  [lat lon]
  {:pre [(instance? java.lang.String lat)
         (instance? java.lang.String lon)]}
  [(read-string lat) (read-string lon)])

(defn latlon-valid?
  "Return true if lat and lon are valid, otherwise return false."
  [lat lon]
  (try
    (let [{:keys [lat-min lat-max lon-min lon-max]} latlon-range
          [lat lon] (read-latlon lat lon)]
      (and (<= lat lat-max)
           (>= lat lat-min)
           (<= lon lon-max)
           (>= lon lon-min)))
    (catch Exception e false)))

;; Ordered vector of DarwinCoreRecord fields for use in wide Cascalog sources.
(def rec-fields ["?occ-id" "?id" "?associatedmedia" "?associatedoccurrences"
                "?associatedreferences" "?associatedsequences" "?associatedtaxa"
                "?basisofrecord" "?bed" "?behavior" "?catalognumber"
                "?collectioncode" "?collectionid" "?continent"
                "?coordinateprecision" "?coordinateuncertaintyinmeters" "?country"
                "?countrycode" "?county" "?datageneralizations" "?dateidentified"
                "?day" "?decimallatitude" "?decimallongitude" "?disposition"
                "?earliestageorloweststage" "?earliesteonorlowesteonothem"
                "?earliestepochorlowestseries" "?earliesteraorlowesterathem"
                "?earliestperiodorlowestsystem" "?enddayofyear"
                "?establishmentmeans" "?eventattributes" "?eventdate" "?eventid"
                "?eventremarks" "?eventtime" "?fieldnotes" "?fieldnumber"
                "?footprintspatialfit" "?footprintwkt" "?formation"
                "?geodeticdatum" "?geologicalcontextid" "?georeferenceprotocol"
                "?georeferenceremarks" "?georeferencesources"
                "?georeferenceverificationstatus" "?georeferencedby" "?group"
                "?habitat" "?highergeography" "?highergeographyid"
                "?highestbiostratigraphiczone" "?identificationattributes"
                "?identificationid" "?identificationqualifier"
                "?identificationreferences" "?identificationremarks"
                "?identifiedby" "?individualcount" "?individualid"
                "?informationwithheld" "?institutioncode" "?island"
                "?islandgroup" "?latestageorhigheststage"
                "?latesteonorhighesteonothem" "?latestepochorhighestseries"
                "?latesteraorhighesterathem" "?latestperiodorhighestsystem"
                "?lifestage" "?lithostratigraphicterms" "?locality"
                "?locationattributes" "?locationid" "?locationremarks"
                "?lowestbiostratigraphiczone" "?maximumdepthinmeters"
                "?maximumdistanceabovesurfaceinmeters"
                "?maximumelevationinmeters" "?measurementaccuracy"
                "?measurementdeterminedby" "?measurementdetermineddate"
                "?measurementid" "?measurementmethod" "?measurementremarks"
                "?measurementtype" "?measurementunit" "?measurementvalue"
                "?member" "?minimumdepthinmeters"
                "?minimumdistanceabovesurfaceinmeters"
                "?minimumelevationinmeters" "?month" "?occurrenceattributes"
                "?occurrencedetails" "?occurrenceid" "?occurrenceremarks"
                "?othercatalognumbers" "?pointradiusspatialfit" "?preparations"
                "?previousidentifications" "?recordnumber" "?recordedby"
                "?relatedresourceid" "?relationshipaccordingto"
                "?relationshipestablisheddate" "?relationshipofresource"
                "?relationshipremarks" "?reproductivecondition" "?resourceid"
                "?resourcerelationshipid" "?samplingprotocol" "?sex"
                "?startdayofyear" "?stateprovince" "?taxonattributes"
                "?typestatus" "?verbatimcoordinatesystem" "?verbatimcoordinates"
                "?verbatimdepth" "?verbatimelevation" "?verbatimeventdate"
                "?verbatimlatitude" "?verbatimlocality" "?verbatimlongitude"
                "?waterbody" "?year" "?footprintsrs" "?georeferenceddate"
                "?identificationverificationstatus" "?institutionid"
                "?locationaccordingto" "?municipality" "?occurrencestatus"
                "?ownerinstitutioncode" "?samplingeffort" "?verbatimsrs"
                "?locationaccordingto7" "?taxonid" "?taxonconceptid" "?datasetid"
                "?datasetname" "?source" "?modified" "?accessrights" "?rights"
                "?rightsholder" "?language" "?higherclassification" "?kingdom"
                "?phylum" "?classs" "?order" "?family" "?genus" "?subgenus"
                "?specificepithet" "?infraspecificepithet" "?scientificname"
                "?scientificnameid" "?vernacularname" "?taxonrank"
                "?verbatimtaxonrank" "?infraspecificmarker"
                "?scientificnameauthorship" "?nomenclaturalcode"
                "?namepublishedin" "?namepublishedinid" "?taxonomicstatus"
                "?nomenclaturalstatus" "?nameaccordingto" "?nameaccordingtoid"
                "?parentnameusageid" "?parentnameusage" "?originalnameusageid"
                "?originalnameusage" "?acceptednameusageid" "?acceptednameusage"
                "?taxonremarks" "?dynamicproperties" "?namepublishedinyear"])

;; Database columns
(def occ-columns
  ["tax_loc_id" "occ_id"
   "id" "associatedmedia" "associatedoccurrences" "associatedreferences"
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
   "georeferencedby" "_group" "habitat" "highergeography" "highergeographyid"
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
   "verbatimelevation" "verbatimeventdate" "verbatimlatitude" "verbatimlocality"
   "verbatimlongitude" "waterbody" "year" "footprintsrs" "georeferenceddate"
   "identificationverificationstatus" "institutionid" "locationaccordingto"
   "municipality" "occurrencestatus" "ownerinstitutioncode" "samplingeffort"
   "verbatimsrs" "locationaccordingto7" "taxonid" "taxonconceptid" "datasetid"
   "datasetname" "source" "modified" "accessrights" "rights" "rightsholder"
   "language" "higherclassification" "kingdom" "phylum" "_classs" "_order" "family"
   "genus" "subgenus" "specificepithet" "infraspecificepithet" "scientificname"
   "scientificnameid" "vernacularname" "taxonrank" "verbatimtaxonrank"
   "infraspecificmarker" "scientificnameauthorship" "nomenclaturalcode"
   "namepublishedin" "namepublishedinid" "taxonomicstatus" "nomenclaturalstatus"
   "nameaccordingto" "nameaccordingtoid" "parentnameusageid" "parentnameusage"
   "originalnameusageid" "originalnameusage" "acceptednameusageid"
   "acceptednameusage" "taxonremarks" "dynamicproperties" "namepublishedinyear"])
