(ns gulo.gbif-test
  "Unit test the gulo.core namespace."
  (:use gulo.gbif
        [cascalog.api]
        [cascalog.more-taps :as taps :only (hfs-delimited)]
        [midje sweet cascalog]
        [clojure.java.io :as io]
        [clojure.contrib.java-utils :only (delete-file-recursively)])
  (:require [clojure.string :as s])
  (:import [com.google.common.io Files]
           [org.gbif.dwc.record DarwinCoreRecord]))

(def loc-data [["" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "41.576233" "-70.6336" "" "" ""]
               ["" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "41.576233" "-70.6336" "" "" ""]
               ["" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "41.576233" "-70.6336" "" "" ""]])

(def name-data [["" "" "" "" "" "" "" "" "" "Puma" "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                ["" "" "" "" "" "" "" "" "" "Puma" "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                ["" "" "" "" "" "" "" "" "Cat" "Puma" "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]])

(def occ-data [["" "" "" "" "" "" "" "" "" "Puma" "" "" "" "" "" "" "" "" "" "" "41.576233" "-70.6336" "" "" ""]
               ["" "" "" "" "" "" "" "" "" "Puma" "" "" "" "" "" "" "" "" "" "" "41.576233" "-70.6336""" "" ""]
               ["" "" "" "" "" "" "" "" "Cat" "Puma" "" "" "" "" "" "" "" "" "" "" "41.576233" "-70.6336""" "" ""]
               ["" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "41.576233" "-70.6336""" "" ""]])

(fact?-
 "Duplicate locations should return a single location."
 [["41.576233" "-70.6336"]]
 (loc-query loc-data :with-uuid false))

(fact?-
 "Duplicate names should return a single name."
 [["" "" "" "" "" "" "Puma"]
  ["" "" "" "" "" "Cat" "Puma"]]
 (tax-query name-data :with-uuid false))

(fact
  "Check number of taxloc results."
  (let [tax (tax-query name-data)
        loc (loc-query loc-data)
        results (??- (taxloc-query tax loc occ-data))]
    (count (first results)) => 2))

(fact?-
 [["41.576233" "-70.6336"]]
 (<- [?uuid ?lat ?lon]
     (hfs-seqfile "/tmp/loc") ?uuid ?lat ?lon))
