(ns gulo.views-test
  (:use gulo.views :reload)
  (:use [cascalog.api]
        [gulo.thrift :as t]
        [gulo.hadoop.pail :as p]
        [midje sweet cascalog])
  (:require [cascalog.ops :as c]
            [clojure.java.io :as io]))

(def src (hfs-textline (.getPath (io/resource "test-stats"))))

(defn query-runner
  [fun k pail-path]
  (let [tap (p/split-chunk-tap pail-path (k stats-paths))]
    (fun tap)))

(fact "Checks get-unique-sci-names"
  (let []
    (get-unique-sci-names src))
  => (produces [["Mustache Mustachus"] ["Puma Concolor"]]))

(fact "Checks get-unique-occurrences"
  (let [src (p/split-chunk-tap PAIL-PATH ["prop" "RecordProperty" "Occurrence"])]
    (get-unique-occurrences src)) => (produces [["myid"] ["yourid"]]))

(fact "Checks get-unique-occ-by-country"
  (let [src (p/split-chunk-tap PAIL-PATH ["prop" "RecordProperty" "Location"])]
    (get-unique-occ-by-country src))
  => (produces [["yourid" "Mustandia"] ["myid" "United States"]]))

(fact "Checks get-unique-by-coll-code"
  (let [src (p/split-chunk-tap PAIL-PATH ["prop" "RecordProperty" "RecordLevel"])]
    (get-unique-by-coll-code src))
  => (produces [["MVZ" "myid"] ["VIZZ" "yourid"]]))

(fact "Checks get-unique-by-occ-class"
  (let [src (p/split-chunk-tap PAIL-PATH ["prop" "RecordProperty" "Taxon"])]
    (get-unique-by-occ-class src))
    => (produces [["myid" "Mammalia"] ["yourid" "Mustachia"]]))

(fact "Checks get-unique-publishers"
  (let [src (p/split-chunk-tap PAIL-PATH ["prop" "OrganizationProperty"])]
    (get-unique-publishers src)) => (produces [["AMNHguid"] ["MVZguid"]]))

(fact "Checks total-occ-by-country-query."
  (query-runner total-occ-by-country-query :records-by-country PAIL-PATH)
  => (produces [["Mustandia" 1] ["United States" 1]]))

(fact "Checks total-occurrences-query."
  (query-runner total-occurrences-query :total-records PAIL-PATH)
  => (produces [[2]]))

(fact
 "Checks total-pubishers-query"
 (query-runner total-publishers-query :total-publishers PAIL-PATH)
 => (produces [[2]]))

(fact "Checks taxa-count-query."
  (query-runner taxa-count-query :total-taxa PAIL-PATH)
  => (produces [[2]]))

(fact "Checks total-by-collection-query."
  (query-runner total-by-collection-query :records-by-collection PAIL-PATH)
  => (produces [["MVZ" 1] ["VIZZ" 1]]))

(fact "Checks total-by-class-query."
  (query-runner total-by-class-query :records-by-class PAIL-PATH)
  => (produces [["Mammalia" 1] ["Mustachia" 1]]))
