(ns gulo.core-test
  (:use gulo.core
        [dwca.core]
        [midje sweet]
        [clojure.string :only (split)]
        [clojure.java.io :as io])
  (:import [com.google.common.io Files]))

;; (fact
;;   "Check harvesting."
;;   (let [source [["http://vertnet.nhm.ku.edu:8080/ipt/archive.do?r=ttrs_mammals"]]
;;         temp-dir (Files/createTempDir)
;;         sink-path (.getPath temp-dir)]
;;     (harvest source sink-path)
;;     (println sink-path)
;;     (count (split (slurp (str sink-path "/part-00000")) #"\n")) => 968))

(fact
  "Check occurrence table."
  (let [source (get-records (->> (io/resource "test-archive") .getPath))
        temp-dir (Files/createTempDir)
        sink-path (.getPath temp-dir)]
    (occurrence-table source sink-path)
    (let [rows (split (slurp (str sink-path "/part-00000")) #"\n")
          row (first rows)
          vals (split row #"\t")
          uuid-pattern #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"]
      (count rows) => 1
      (count vals) => 183
      (count (filter #(not (= % "_")) vals)) => 6
      (re-matches uuid-pattern (nth vals 0)) => truthy
      (nth vals 1) => "1"
      (nth vals 22) => "40"
      (nth vals 23) => "-122"
      (nth vals 73) => "Berkeley, California, USA"
      (nth vals 160) => "Puma concolor")))

(fact
  "Check occurrence table."
  (let [source (get-records (->> (io/resource "test-archive") .getPath))
        temp-dir (Files/createTempDir)
        temp-path (.getPath temp-dir)
        occ-path (str temp-path "/occ")
        tax-path (str temp-path "/tax")]
    (occurrence-table source sink-path)
    
    (let [rows (split (slurp (str sink-path "/part-00000")) #"\n")
          row (first rows)
          vals (split row #"\t")
          uuid-pattern #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"]
      (count rows) => 1
      (count vals) => 183
      (count (filter #(not (= % "_")) vals)) => 6
      (re-matches uuid-pattern (nth vals 0)) => truthy
      (nth vals 1) => "1"
      (nth vals 22) => "40"
      (nth vals 23) => "-122"
      (nth vals 73) => "Berkeley, California, USA"
      (nth vals 160) => "Puma concolor")))
