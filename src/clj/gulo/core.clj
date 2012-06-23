(ns gulo.core
  "Cascalog queries for transforming CSV Darwin Core data into CartoDB tables."
  (:use [gulo.util :as util :only (latlon-valid? gen-uuid)]
        [cascalog.api]
        [cascalog.more-taps :as taps :only (hfs-delimited)]
        [dwca.core :as dwca])
  (:import [org.gbif.dwc.record DarwinCoreRecord]))

;; Position of values in a split texline.
(def OCC-ID 0)
(def LAT 22)
(def LON 23)
(def SCINAME 160)

(defn- makeline
  "Returns a string line by joining a sequence of values on tab."
  [& vals]
  (clojure.string/join \tab vals))

(defn- splitline
  "Returns vector of line values by splitting on tab."
  [line]
  (clojure.string/split line #"\t" -1))

(defn- line->loc
  "Return 3-tuple [occid lat lon] from supplied textline."
  [line]
  (let [vals (splitline line)
        occid (nth vals OCC-ID)
        lat (nth vals LAT)
        lon (nth vals LON)]
    [occid lat lon]))

(defn- line->name
  "Return 2-tuple [occid scientificname] from supplied textline."
  [line]
  (let [vals (splitline line)
        occid (nth vals OCC-ID)
        name (nth vals SCINAME)]
  [occid name]))

(defn- line->locname
  "Return 4-tuple [occid lat lon name] from supplied textline."
  [line]
  (let [[occid lat lon] (line->loc line)
        [occid name] (line->name line)]
    [occid lat lon name]))

(defn occ-table
  "Build occ and tax_loc tables."
  [occ-path tax-path loc-path tax-loc-path occ-sink-path]
  (let [fields util/rec-fields
        result-vector (vec (cons "?tax-loc-id" fields))
        occ-tab (hfs-textline occ-path)
        occ-source (hfs-textline occ-path)
        tax (hfs-textline tax-path)
        loc (hfs-textline loc-path)
        tax-loc-source (hfs-textline tax-loc-path)
        occ-sink (taps/hfs-delimited occ-sink-path :sinkmode :replace)        
        tax-loc-occ (<- [?taxon-id ?loc-id ?occ-id ?name ?lat ?lon]
                        (tax ?tax-line)
                        (splitline ?tax-line :> ?taxon-id ?name)
                        (loc ?loc-line)
                        (splitline ?loc-line :> ?loc-id ?lat ?lon _)
                        (occ-tab ?occ-line)
                        (line->locname ?occ-line :> ?occ-id ?lat ?lon ?name))]

    (?<- occ-sink
         result-vector
         (tax-loc-occ ?taxon-id ?loc-id ?occ-id ?scientificname ?decimallatitude ?decimallongitude)
         (tax-loc-source ?tax-loc-line)
         (splitline ?tax-loc-line :> ?tax-loc-id ?taxon-id ?loc-id)
         (occ-tab ?occ-line)
         (splitline ?occ-line :>> fields))))

(defn tax-loc-table
  "Build tax_loc table."
  [occ-path tax-path loc-path tax-loc-sink-path]
  (let [occ-tab (hfs-textline occ-path)
        tax (hfs-textline tax-path)
        loc (hfs-textline loc-path)
        tax-loc-sink (hfs-textline tax-loc-sink-path :sinkmode :replace)
        tax-loc-occ (<- [?taxon-id ?loc-id]
                        (tax ?tax-line)
                        (splitline ?tax-line :> ?taxon-id ?name)
                        (loc ?loc-line)
                        (splitline ?loc-line :> ?loc-id ?lat ?lon _)
                        (occ-tab ?occ-line)
                        (line->locname ?occ-line :> ?occ-id ?lat ?lon ?name)
                        (:distinct true))]
    (?<- tax-loc-sink
         [?line]
         (tax-loc-occ ?taxon-id ?loc-id)
         (util/gen-uuid :> ?tax-loc-id)
         (makeline ?tax-loc-id ?taxon-id ?loc-id :> ?line))))

(deffilterop valid-name?
  "Return true if name is valid, otherwise return false."
  [name]
  (not= name "_"))

(defn taxon-table
  "Create taxon table with unique [uuid name] from supplied source of Darwin Core
  textlines."
  [source sink-path]
  (let [sink (hfs-textline sink-path :sinkmode :replace)
        uniques (<- [?name]
                     (source ?line)
                     (line->name ?line :> _ ?name)
                     (:distinct true))]
    (?<- sink
         [?line]
         (uniques ?name)
         (util/gen-uuid :> ?uuid)
         (makeline ?uuid ?name :> ?line))))

(defn location-table
  "Create location table with unique lines [uuid lat lon wkt] from supplied
  source of Darwin Core textlines."
  [source sink-path]
  (let [sink (hfs-textline sink-path :sinkmode :replace)
        uniques (<- [?lat ?lon]
                    (source ?line)
                    (line->loc ?line :> _ ?lat ?lon)
                    (util/latlon-valid? ?lat ?lon)
                    (:distinct true))]
    (?<- sink
         [?line]
         (uniques ?lat ?lon)
         (util/wkt-point ?lat ?lon :> ?wkt)
         (util/gen-uuid :> ?uuid)
         (makeline ?uuid ?lat ?lon ?wkt :> ?line)
         (:distinct true))))
