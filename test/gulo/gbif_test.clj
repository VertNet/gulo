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

(def uuid-pattern #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
(def uuid-str "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")

(fact
  "Test loc-query."
  (let [source-path (->> (io/resource "line.csv") .getPath)
        source (hfs-textline source-path)
        sink-path (str (->> (io/resource "test") .getPath) "/loc")
        match (re-pattern (str uuid-str "\\t9.931666\\t0.89666665\\tPOINT\\(0.89666665 9.931666\\)"))]
    (loc-query (hfs-textline source-path) sink-path)
    (slurp (str sink-path "/part-00000") :encoding "UTF-8") => match))

(fact
  "Test tax-query."
  (let [source-path (->> (io/resource "line.csv") .getPath)
        source (hfs-textline source-path)
        sink-path (str (->> (io/resource "test") .getPath) "/tax")
        match (re-pattern (str uuid-str "\\tBacteria\\tAcidobacteria\\tAcidobacteria\\t\\\\N\\t\\\\N\\t\\\\N\\tAcidobacteria\n"))]
    (tax-query (hfs-textline source-path) sink-path)
    (slurp (str sink-path "/part-00000") :encoding "UTF-8") => match))

(fact
  "Test taxloc-query. Depends on outputs of previous facts for loc-query and
  tax-query."
  (let [base-path (->> (io/resource "test") .getPath)
        occ-path (->> (io/resource "line.csv") .getPath)        
        tax-path (str base-path "/tax")
        loc-path (str base-path "/loc")
        sink-path (str base-path "/taxloc")
        tax-uuid (first (.split (slurp (str tax-path "/part-00000")) "\\t"))
        loc-uuid (first (.split (slurp (str loc-path "/part-00000")) "\\t"))
        match (re-pattern (s/join "\\t" [uuid-str tax-uuid loc-uuid]))]
    (taxloc-query occ-path tax-path loc-path sink-path)
    (slurp (str sink-path "/part-00000") :encoding "UTF-8") => match))

(fact
  "Test taxloc-query. Depends on outputs of previous facts for loc-query and
  tax-query and taxloc-query."
  (let [base-path (->> (io/resource "test") .getPath)
        occ-path (->> (io/resource "line.csv") .getPath)        
        tax-path (str base-path "/tax")
        loc-path (str base-path "/loc")
        taxloc-path (str base-path "/taxloc")
        sink-path (str base-path "/occ")
        taxloc-uuid (first (.split (slurp (str taxloc-path "/part-00000")) "\\t"))
        match (re-pattern (str taxloc-uuid "\\t242135095\\t164\\t432\\tBacteria\\tAcidobacteria\\tAcidobacteria\\t\\\\N\\t\\\\N\\t\\\\N\\tAcidobacteria\\tBacteria\\tAcidobacteria\\tAcidobacteria\\t\\\\N\\t\\\\N\\t\\\\N\\tAcidobacteria\\t\\\\N\\t2005-03-11\\t2005\\t3\\t0\\t\\\\N\\tTG\\t\\\\N\\t\\\\N\\t\\\\N\\t\\\\N\\t9.93166666667\\t9.931666\\t0.896666666667\\t0.89666665\\t\\\\N\\t0\\t2012-07-05 00:29:09\n"))]
    (occ-query occ-path tax-path loc-path taxloc-path sink-path)
    (slurp (str sink-path "/part-00000") :encoding "UTF-8") => match))
