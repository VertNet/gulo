(ns gulo.gbif
  "This namespace provides support for GBIF data."
  (:use [gulo.core :as core]
        [gulo.util :as util :only (latlon-valid? gen-uuid)]
        [cascalog.api]
        [cascalog.more-taps :as taps :only (hfs-delimited)]
        [dwca.core :as dwca])
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (:import [org.gbif.dwc.record DarwinCoreRecord]))

;; Ordered column names from the occurrence_20120802.txt.gz dump.
(def occ-fields ["?occurrenceid" "?taxonid" "?dataresourceid" "?kingdom"
                 "?phylum" "?class" "?orderrank" "?family" "?genus"
                 "?scientificname" "?kingdomoriginal" "?phylumoriginal"
                 "?classoriginal" "?orderrankoriginal" "?familyoriginal"
                 "?genusoriginal" "?scientificnameoriginal" "?authororiginal"
                 "?datecollected" "?year" "?month" "?basisofrecord"
                 "?countryoriginal" "?countryisointerpreted" "?locality"
                 "?county" "?continentorocean" "?stateprovince" "?latitude"
                 "?latitudeinterpreted" "?longitude" "?longitudeinterpreted"
                 "?coordinateprecision" "?geospatialissue" "?lastindexed"])

;; Ordered column names for MOL schema.
(def mol-fields ["?occurrenceid" "?taxonid" "?dataresourceid" "?kingdom"
                 "?phylum" "?class" "?orderrank" "?family" "?genus"
                 "?scientificname" "?datecollected" "?year" "?month"
                 "?basisofrecord" "?countryisointerpreted" "?locality"
                 "?county" "?continentorocean" "?stateprovince"
                 "?latitudeinterpreted" "?longitudeinterpreted"
                 "?coordinateprecision" "?geospatialissue" "?lastindexed"])

;; Position of values in a texline.
(def OCCID 0)
(def KINGDOM 3)
(def PHYLUM 4)
(def CLASS 5)
(def ORDER 6)
(def FAMILY 7)
(def GENUS 8)
(def SNAME 9)
(def LATI 29)
(def LONI 31)

(defn split-line
  "Returns vector of line values by splitting on tab."
  [line]
  (vec (.split line "\t")))

(defn id
  "Return occid from supplied textline."
  [line]
  (nth (split-line line) OCCID))

(defn loc
  "Return 3-tuple [occid lat lon] from supplied textline."
  [line]
  (let [vals (split-line line)]
    (map (partial nth vals) [LATI LONI])))

(defn tax
  "Return 7-tuple [kingdom phylum class order family genus scientificname] from
  supplied textline."
  [line]
  (let [vals (split-line line)]
    (map (partial nth vals) [KINGDOM PHYLUM CLASS ORDER FAMILY GENUS SNAME])))

(defn locname
  "Return 4-tuple [occid lat lon name] from supplied textline."
  [line]
  (let [[lat lon] (loc line)
        [k p c o f g s] (tax line)]
    [lat lon k p c o f g s]))

(defn occ-query
  "Execute query against supplied source of occ, loc, tax, and taxloc textlines
  for occurrence rows that include its taxloc uuid link. Sinks rows to supplied
  sink path."
  [occ-path tax-path loc-path taxloc-path sink-path & {:keys [fields] :or {fields occ-fields}}]
  (let [result-vector (vec (cons "?taxloc-id" fields))
        occ-source (hfs-textline occ-path)
        tax-source (hfs-textline tax-path)
        loc-source (hfs-textline loc-path)
        taxloc-source (hfs-textline taxloc-path)
        sink (taps/hfs-delimited sink-path :sinkmode :replace)        
        uniques (<- [?tax-id ?loc-id ?occurrenceid ?k ?p ?c ?o ?f ?g ?s ?lat ?lon]
                    (tax-source ?tax-line)
                    (split-line ?tax-line :> ?tax-id ?k ?p ?c ?o ?f ?g ?s)
                    (loc-source ?loc-line)
                    (split-line ?loc-line :> ?loc-id ?lat ?lon _)
                    (occ-source ?occ-line)
                    (id ?occ-line :> ?occurrenceid)
                    (locname ?occ-line :> ?lat ?lon ?k ?p ?c ?o ?f ?g ?s)
                    (occ-source ?occ-line))]

    (?<- sink
         result-vector
         (uniques ?tax-id ?loc-id ?occurrenceid ?kingdom ?phylum ?class ?orderrank
                  ?family ?genus ?scientificname ?latitudeinterpreted
                  ?longitudeinterpreted)
         (taxloc-source ?taxloc-line)
         (split-line ?taxloc-line :> ?taxloc-id ?tax-id ?loc-id)
         (occ-source ?occ-line)
         (split-line ?occ-line :>> occ-fields))))

(defn taxloc-query
  "Execute query against supplied source of loc, tax, and occurrence textlines
  for unique links between taxonomies and their locations. Sinks tuples [uuid
  taxid locid]."
  [occ-path tax-path loc-path sink-path]
  (let [occ-source (hfs-textline occ-path)
        tax-source (hfs-textline tax-path)
        loc-source (hfs-textline loc-path)
        sink (hfs-textline sink-path :sinkmode :replace)
        uniques (<- [?tax-id ?loc-id]
                    (tax-source ?tax-line)
                    (split-line ?tax-line :> ?tax-id ?k ?p ?c ?o ?f ?g ?s)
                    (loc-source ?loc-line)
                    (split-line ?loc-line :> ?loc-id ?lat ?lon _)
                    (occ-source ?occ-line)
                    (locname ?occ-line :> ?lat ?lon ?k ?p ?c ?o ?f ?g ?s)
                    (:distinct true))]
    (?<- sink
         [?line]
         (uniques ?tax-id ?loc-id)
         (util/gen-uuid :> ?taxloc-id)
         (makeline ?taxloc-id ?tax-id ?loc-id :> ?line))))

(defn tax-query
  "Execute query against supplied source of occurrence textlines for unique
  taxonomies. Sink tuples [uuid kingdom phylum class order family genus scientificname]
  to sink-path."
  [source sink-path]
  (let [sink (hfs-textline sink-path :sinkmode :replace)
        uniques (<- [?k ?p ?c ?o ?f ?g ?s]
                     (source ?line)
                     (tax ?line :> ?k ?p ?c ?o ?f ?g ?s)
                     (core/valid-name? ?s)
                     (:distinct true))]
    (?<- sink
         [?line]
         (uniques ?k ?p ?c ?o ?f ?g ?s)
         (util/gen-uuid :> ?uuid)
         (makeline ?uuid ?k ?p ?c ?o ?f ?g ?s :> ?line))))

(defn loc-query
  "Execute query against supplied source of occurrence textlines for unique
  coordinates. Sink tuples [uuid lat lon wkt] to sink-path."
  [source sink-path]
  (let [sink (hfs-textline sink-path :sinkmode :replace)
        uniques (<- [?lat ?lon]
                    (source ?line)
                    (loc ?line :> ?lat ?lon)
                    (util/latlon-valid? ?lat ?lon)
                    (:distinct true))]
    (?<- sink
         [?line]
         (uniques ?lat ?lon)
         (util/wkt-point ?lat ?lon :> ?wkt)
         (util/gen-uuid :> ?uuid)
         (core/makeline ?uuid ?lat ?lon ?wkt :> ?line)
         (:distinct true))))
