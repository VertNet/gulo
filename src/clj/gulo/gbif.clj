(ns gulo.gbif
  "This namespace provides support for GBIF data."
  (:use [cascalog.api]
        [cascalog.more-taps :as taps :only (hfs-delimited)]
        [dwca.core :as dwca])
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (:import [org.gbif.dwc.record DarwinCoreRecord]))

;; Ordered column names from the occurrence_20120802.txt.gz GBIF dump.
(def gbif-fields ["?occurrenceid" "?taxonid" "?dataresourceid" "?kingdom"
                 "?phylum" "?class" "?orderrank" "?family" "?genus"
                 "?scientificname" "?kingdomoriginal" "?phylumoriginal"
                 "?classoriginal" "?orderrankoriginal" "?familyoriginal"
                 "?genusoriginal" "?scientificnameoriginal" "?authororiginal"
                 "?datecollected" "?year" "?month" "?basisofrecord"
                 "?countryoriginal" "?countryisointerpreted" "?locality"
                 "?county" "?continentorocean" "?stateprovince" "?latitude"
                 "?latitudeinterpreted" "?longitude" "?longitudeinterpreted"
                 "?coordinateprecision" "?geospatialissue" "?lastindexed"])

;; Ordered column names for MOL master dataset schema.
(def mol-fields ["?uuid" "?occurrenceid" "?taxonid" "?dataresourceid" "?kingdom"
                 "?phylum" "?class" "?orderrank" "?family" "?genus"
                 "?scientificname" "?datecollected" "?year" "?month"
                 "?basisofrecord" "?countryisointerpreted" "?locality"
                 "?county" "?continentorocean" "?stateprovince"
                 "?latitudeinterpreted" "?longitudeinterpreted"
                 "?coordinateprecision" "?geospatialissue" "?lastindexed"])

;; Ordered column names for MOL occ table schema.
(def occ-fields ["?taxloc-uuid" "?uuid" "?occurrenceid" "?taxonid"
                 "?dataresourceid" "?datecollected" "?year" "?month"
                 "?basisofrecord" "?countryisointerpreted" "?locality" "?county"
                 "?continentorocean" "?stateprovince" "?coordinateprecision"
                 "?geospatialissue" "?lastindexed"])

(defn makeline
  "Returns a string line by joining a sequence of values on tab."
  [& vals]
  (clojure.string/join \tab vals))

(defn split-line
  "Returns vector of line values by splitting on tab."
  [line]
  (vec (.split line "\t")))

(defn gen-uuid
  "Return a randomly generated UUID string."
  [& x]
  (str (java.util.UUID/randomUUID)))

(defn valid-latlon?
  "Return true if lat and lon are valid decimal degrees,
   otherwise return false. Assumes that lat and lon are both either numeric
   or string."
  [lat lon]
  (if (or (= "" lat)
          (= "" lon))
    false   
    (try
      (let [[lat lon] (if (number? lat)
                        [lat lon]
                        (map read-string [lat lon]))
            latlon-range {:lat-min -90 :lat-max 90 :lon-min -180 :lon-max 180}
            {:keys [lat-min lat-max lon-min lon-max]} latlon-range]
        (and (<= lat lat-max)
             (>= lat lat-min)
             (<= lon lon-max)
             (>= lon lon-min)))
      (catch Exception e false))))

(defn valid-name?
  "Return true if name is valid, otherwise return false."
  [name]
  (and (not= name nil) (not= name "")))

(defn read-occurrences
  "Return Cascalog generator of GBIF tuples with valid Scientific name and
   coordinates."
  [path]
  (let [src (hfs-textline path)]
    (<- mol-fields
        (src ?line)
        (gen-uuid :> ?uuid)
        (clojure.string/replace ?line "\\N" "" :> ?clean-line)
        (split-line ?clean-line :>> gbif-fields)
        (valid-latlon? ?latitudeinterpreted ?longitudeinterpreted)
        (valid-name? ?scientificname))))

(defn occ-query
  "Return generator of unique occurrences with a taxloc-id."
  [tax-source loc-source taxloc-source occ-source]
  (let [uniques (<- [?tax-uuid ?loc-uuid ?occurrenceid ?scientificname ?kingdom
                     ?phylum ?class ?orderrank ?family ?genus ?latitudeinterpreted
                     ?longitudeinterpreted]
                    (tax-source ?tax-uuid ?scientificname ?kingdom ?phylum ?class ?orderrank ?family ?genus)
                    (loc-source ?loc-uuid ?latitudeinterpreted ?longitudeinterpreted)
                    (occ-source :>> mol-fields)
                    (:distinct true))]
    (<- occ-fields
        (uniques ?tax-uuid ?loc-uuid ?occurrenceid ?scientificname ?kingdom
                 ?phylum ?class ?orderrank ?family ?genus ?latitudeinterpreted
                 ?longitudeinterpreted)
        (taxloc-source ?taxloc-uuid ?tax-uuid ?loc-uuid)
        (occ-source :>> mol-fields))))

(defn taxloc-query
  "Return generator of unique taxonomy locations from supplied source of unique
  taxonomies (via tax-query), unique locations (via loc-query), and occurrence
  source of mol-fields."
  [tax-source loc-source occ-source & {:keys [with-uuid] :or {with-uuid true}}]
  (let [occ (<- [?latitudeinterpreted ?longitudeinterpreted ?scientificname
                 ?kingdom ?phylum ?class ?orderrank ?family ?genus]
                (occ-source :>> mol-fields))
        uniques (<- [?tax-uuid ?loc-uuid]
                    (tax-source ?tax-uuid ?s ?k ?p ?c ?o ?f ?g)
                    (loc-source ?loc-uuid ?lat ?lon)
                    (occ ?lat ?lon ?s ?k ?p ?c ?o ?f ?g)
                    (:distinct true))]
    (if with-uuid
      (<- [?uuid ?tax-uuid ?loc-uuid]
          (uniques ?tax-uuid ?loc-uuid)
          (gen-uuid :> ?uuid))
      uniques)))

(defn tax-query
  "Return generator of unique taxonomy tuples from supplied source of mol-fields.
   Assumes sounce contains valid ?scientificname."
  [source & {:keys [with-uuid] :or {with-uuid true}}]  
  (let [uniques (<- [?scientificname ?kingdom ?phylum ?class ?orderrank ?family ?genus]
                    (source :>> mol-fields)
                    (:distinct true))]
    (if with-uuid
      (<- [?uuid ?s ?k ?p ?c ?o ?f ?g]
          (uniques ?s ?k ?p ?c ?o ?f ?g)
          (gen-uuid :> ?uuid))
      uniques)))

(defn loc-query
  "Return generator of unique coordinate tuples from supplied source of
   mol-fields. Assumes source contains valid coordinates."
  [source & {:keys [with-uuid] :or {with-uuid true}}]
  (let [uniques (<- [?latitudeinterpreted ?longitudeinterpreted]
                    (source :>> mol-fields)
                    (:distinct true))]
    (if with-uuid
      (<- [?uuid ?lat ?lon]
          (uniques ?lat ?lon)
          (gen-uuid :> ?uuid))
      uniques)))

(defn build-master-dataset
  "Convert raw GBIF data into seqfiles in MoL schema with invalid records filtered
   out."
  [& {:keys [source-path sink-path]
      :or {source-path (.getPath (io/resource "occ.txt"))
           sink-path "/tmp/mds"}}]
  (let [query (read-occurrences source-path)]
    (?- (hfs-seqfile sink-path :sinkmode :replace) query)))

(defn build-cartodb-schema
  [& {:keys [source-path sink-path with-uuid]
      :or {source-path "/tmp/mds"
           sink-path "/tmp"
           with-uuid true}}]
  (let [source (hfs-seqfile source-path)
        loc-path (format "%s/loc" sink-path)
        loc-sink (hfs-seqfile loc-path :sinkmode :replace)
        tax-path (format "%s/tax" sink-path)
        tax-sink (hfs-seqfile tax-path :sinkmode :replace)
        taxloc-path (format "%s/taxloc" sink-path)
        taxloc-sink (hfs-seqfile taxloc-path :sinkmode :replace)
        occ-path (format "%s/occ" sink-path)
        occ-sink (hfs-seqfile occ-path :sinkmode :replace)]
    (?- loc-sink (loc-query source))
    (?- tax-sink (tax-query source))
    (?- taxloc-sink (taxloc-query tax-sink loc-sink source))
    (?- occ-sink (occ-query tax-sink loc-sink taxloc-sink source))))

(defn build-cartodb-views
  [& {:keys [source-path sink-path]
      :or {source-path "/tmp"
           sink-path "/tmp/cdb"}}]
  (let [loc-sink (hfs-textline (format "%s/loc" sink-path) :sinkmode :replace)
        loc-source (hfs-seqfile (format "%s/loc" source-path))
        tax-sink (hfs-textline (format "%s/tax" sink-path) :sinkmode :replace)
        tax-source (hfs-seqfile (format "%s/tax" source-path))
        taxloc-sink (hfs-textline (format "%s/taxloc" sink-path) :sinkmode :replace)
        taxloc-source (hfs-seqfile (format "%s/taxloc" source-path))
        occ-sink (hfs-textline (format "%s/occ" sink-path) :sinkmode :replace)
        occ-source (hfs-seqfile (format "%s/occ" source-path))]
    (?- loc-sink loc-source)
    (?- tax-sink tax-source)
    (?- taxloc-sink taxloc-source)
    (?- occ-sink occ-source)))

(defmain BuildMasterDataset
  [source-path sink-path]
  (build-master-dataset :source-path source-path :sink-path sink-path))

(defmain BuildCartoDBSchema
  [source-path sink-path]
  (build-cartodb-schema :source-path source-path :sink-path sink-path))

(defmain BuildCartoDBViews
  [source-path sink-path]
  (build-cartodb-views :source-path source-path :sink-path sink-path))

(comment
  (let [dq (read-occurrences (.getPath (io/resource "occ-test.tsv")))
        lq (loc-query dq)
        tq (tax-query dq)]
    (?- (hfs-seqfile "/tmp/loc" :sinkmode :replace) lq)
    (?- (hfs-seqfile "/tmp/tax" :sinkmode :replace) tq)
    (?- (hfs-seqfile "/tmp/data" :sinkmode :replace) dq)
    (let [lq-source (hfs-seqfile "/tmp/loc")
          tq-source (hfs-seqfile "/tmp/tax")
          d-source (hfs-seqfile "/tmp/data")
          tlq (taxloc-query tq-source lq-source d-source)]
      (?- (hfs-seqfile "/tmp/taxloc" :sinkmode :replace) tlq)
      (let [tlq-source (hfs-seqfile "/tmp/taxloc")
            occ-q (occ-query tq-source lq-source tlq-source d-source)]
        (?- (hfs-seqfile "/tmp/occ" :sinkmode :replace) occ-q)))))

