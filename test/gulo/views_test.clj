(ns gulo.views-test
  (:use gulo.views :reload)
  (:use [cascalog.api]
        [gulo.thrift :as t]
        [gulo.hadoop.pail :as p]
        [midje sweet cascalog])
  (:require [cascalog.ops :as c])
  (:import [backtype.hadoop.pail Pail]))

(def PAIL-PATH "/tmp/vn-test-tmp")

(def TEST-RECORDS
  [{:id "myid" :scientificname "Puma Concolor" :country "United States" :collectioncode "MVZ" :clazz "Mammalia"}
   {:id "myid" :scientificname "Puma Concolor" :country "United States" :collectioncode "MVZ" :clazz "Mammalia"}
   {:id "yourid" :scientificname "Mustache Mustachus" :country "Mustandia" :collectioncode "VIZZ" :clazz "Mustachia"}])

(defn sink-test-records [pail-path records]
  (let [dataset-guid "myguid"
        record-data (map (partial t/record-data dataset-guid) records)]
    (for [d record-data]
      (p/to-pail pail-path (<- [?d] (d ?d))))))

(defn delete-directory
  [path]
  (let [fs (file-seq (clojure.java.io/file path))]
    (for [f (reverse fs)]
      (clojure.java.io/delete-file f))))

(defn query-runner
  [fun k pail-path]
  (let [tap (p/split-chunk-tap pail-path (k stats-paths))]
    (fun tap)))

;; TODO: ensure sink-test-records runs before tests
(sink-test-records PAIL-PATH TEST-RECORDS)

(future-fact "Checks unpack-RecordProperty")
(future-fact "Checks get-RecordProperty-id")
(future-fact "Checks unpack-OrganizationProperty")
(future-fact "Checks get-OrganizationProperty-id")
(future-fact "Checks get-org-id")
(future-fact "Checks get-country")
(future-fact "Checks get-scientific-name")
(future-fact "Checks get-collection-code")
(future-fact "Checks get-class")
(future-fact "Checks get-unique-sci-names")
(future-fact "Checks get-unique-occurrences")
(future-fact "Checks get-unique-occ-by-country")
(future-fact "Checks get-unique-publishers")
(future-fact "Checks get-unique-by-coll-code")
(future-fact "Checks get-unique-by-occ-class")

(fact "Checks total-occ-by-country-query."
  (query-runner total-occ-by-country-query :records-by-country PAIL-PATH)
  => (produces [["Mustandia" 1] ["United States" 1]]))

(fact "Checks total-occurrences-query."
  (query-runner total-occurrences-query :total-records PAIL-PATH)
  => (produces [[2]]))

(future-fact "Checks total-pubishers-query (requires creating organization dataset)"
             (query-runner total-publishers-query :total-publishers PAIL-PATH))

(fact "Checks taxa-count-query."
  (query-runner taxa-count-query :total-taxa PAIL-PATH)
  => (produces [[2]]))

(fact "Checks total-by-collection-query."
  (query-runner total-by-collection-query :records-by-collection PAIL-PATH)
  => (produces [["MVZ" 1] ["VIZZ" 1]]))

(fact "Checks total-by-class-query."
  (query-runner total-by-class-query :records-by-class PAIL-PATH)
  => (produces [["Mammalia" 1] ["Mustachia" 1]]))

;; TODO: ensure delete-directory runs after tests
;; (delete-directory PAIL-PATH)
