(ns gulo.core-test
  "Unit test the gulo.core namespace."
  (:use gulo.core
        [gulo.util :only (latlon-valid?)]
        [dwca.core]
        [cascalog.api]
        [cascalog.more-taps :as taps :only (hfs-delimited)]
        [midje sweet]
        [clojure.string :only (split)]
        [clojure.java.io :as io]
        [clojure.contrib.java-utils :only (delete-file-recursively)])
  (:import [com.google.common.io Files]))

(def uuid-pattern #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
(def uuid-pattern-str "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")

(fact "Test taxon-table."
  (let [occ-path (->> (io/resource "dwc-nysm.tsv") .getPath)
        source (taps/hfs-delimited occ-path)
        temp-dir (->> (io/resource "test") .getPath)
        sink-path (str temp-dir "/tax")
        sink-part (str sink-path "/part-00000")
        expected #(slurp (io/resource "test/tax.tsv") :encoding "utf-8")]
    (taxon-table (hfs-textline occ-path) sink-path)
    (slurp sink-part :encoding "utf-8") =>
    #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}	Puma concolor"))

(fact "Test location-table."
  (let [occ-path (->> (io/resource "dwc-nysm.tsv") .getPath)
        source (hfs-textline occ-path)
        temp-dir (->> (io/resource "test") .getPath)
        sink-path (str temp-dir "/loc")
        sink-part (str sink-path "/part-00000")
        expected (slurp (io/resource "test/loc.tsv") :encoding "utf-8")]    
    (location-table (hfs-textline occ-path) sink-path)
    (slurp sink-part :encoding "utf-8") =>
    (just #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}\t40.8\t-122.3\tPOINT(-122.3 40.8)\n")))

(fact "Test tax-loc-table"
  (let [occ-path (->> (io/resource "dwc-nysm.tsv") .getPath)
        source (hfs-textline occ-path)
        temp-dir (->> (io/resource "test") .getPath)
        sink-path (str temp-dir "/tax-loc")
        tax-part (str temp-dir "/tax/part-00000")
        loc-part (str temp-dir "/loc/part-00000")
        sink-part (str sink-path "/part-00000")
        expected (slurp (io/resource "test/tax-loc.tsv") :encoding "utf-8")]
    (tax-loc-table occ-path tax-part loc-part sink-path)
    ;;(slurp sink-part :encoding "utf-8") => expected
    1 => 1
    ))

(fact "Test occ-table"
  (let [occ-path (->> (io/resource "dwc-nysm.tsv") .getPath)
        source (hfs-textline occ-path)
        temp-dir (->> (io/resource "test") .getPath)
        sink-path (str temp-dir "/tax-loc")
        tax-part (str temp-dir "/tax/part-00000")
        loc-part (str temp-dir "/loc/part-00000")
        sink-part (str sink-path "/part-00000")
        expected (slurp (io/resource "test/tax-loc.tsv") :encoding "utf-8")]
    (tax-loc-table occ-path tax-part loc-part sink-path)
    ;;(slurp sink-part :encoding "utf-8") => expected
    1 => 1
    ))

(fact "Test occ-table"
  (let [occ-path (->> (io/resource "dwc-nysm.tsv") .getPath)
        source (hfs-textline occ-path)
        temp-dir (->> (io/resource "test") .getPath)
        sink-path (str temp-dir "/occ")
        tax-part (str temp-dir "/tax/part-00000")
        loc-part (str temp-dir "/loc/part-00000")
        tax-loc-part (str temp-dir "/tax-loc/part-00000")
        sink-part (str sink-path "/part-00000")
        expected (slurp (io/resource "test/tax-loc.tsv") :encoding "utf-8")]
    (occ-table occ-path tax-part loc-part tax-loc-part sink-path)
    1 => 1
    ;;(slurp sink-part :encoding "utf-8") => expected
    ))

;; (fact
;;   "Check occ-tax-loc function."
;;   (let [occ-path (->> (io/resource "nysm_mammals_3.csv") .getPath)
;;         source (taps/hfs-delimited occ-path)
;;         temp-dir (Files/createTempDir)
;;         sink-path (.getPath temp-dir)
;;         occ-sink (str sink-path "/occ")
;;         tax-sink (str sink-path "/tax")
;;         loc-sink (str sink-path "/loc")
;;         tax-loc-sink (str sink-path "/tax-loc")]
;;     (taxon-table source tax-sink)
;;     (location-table source loc-sink)
;;     (occ-tax-loc occ-path
;;                  (str tax-sink "/part-00000")
;;                  (str loc-sink "/part-00000")
;;                  occ-sink
;;                  tax-loc-sink)
;;     (let [rows (split (slurp (str occ-sink "/part-00000")) #"\n")
;;           row (first rows)
;;           vals (split row #"\t")
;;           uuid-pattern #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"]
;;       (count rows) => 2
;;       (count vals) => 184
;;       (re-matches uuid-pattern (nth vals 0)) => truthy
;;       (re-matches uuid-pattern (nth vals 1)) => truthy)
;;     (delete-file-recursively sink-path)))

;; (fact
;;   "Check location-table function."
;;   (let [source (taps/hfs-delimited (->> (io/resource "nysm_mammals_3.csv") .getPath))
;;         temp-dir (Files/createTempDir)
;;         sink-path (.getPath temp-dir)]
;;     (location-table source sink-path)
;;     (let [rows (split (slurp (str sink-path "/part-00000")) #"\n")
;;           row (first rows)
;;           vals (split row #"\t")
;;           uuid-pattern #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"]
;;       (count rows) => 2
;;       (count vals) => 3
;;       (re-matches uuid-pattern (nth vals 0)) => truthy
;;       (latlon-valid? (nth vals 1) (nth vals 2)) => truthy)
;;     (delete-file-recursively temp-dir)))

;; (fact
;;   "Check taxon-table function."
;;   (let [source (taps/hfs-delimited (->> (io/resource "nysm_mammals_3.csv") .getPath))
;;         temp-dir (Files/createTempDir)
;;         sink-path (.getPath temp-dir)]
;;     (taxon-table source sink-path)
;;     (let [rows (split (slurp (str sink-path "/part-00000")) #"\n")
;;           row (first rows)
;;           vals (split row #"\t")
;;           uuid-pattern #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"]
;;       (count rows) => 7
;;       (count vals) => 2
;;       (re-matches uuid-pattern (nth vals 0)) => truthy)
;;     (delete-file-recursively temp-dir)))
