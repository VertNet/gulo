(ns gulo.views-test
  (:use gulo.views :reload)
  (:use [cascalog.api]
        [gulo.thrift :as t]
        [gulo.hadoop.pail :as p]
        [midje sweet cascalog])
  (:require [cascalog.ops :as c]
            [clojure.java.io :as io])
  (:import [backtype.hadoop.pail Pail]
           [gulo.schema
            Data DataUnit DatasetID DatasetProperty DatasetPropertyValue
            DatasetRecordEdge Event GeologicalContext Identification Location
            MeasurementOrFact Occurrence OrganizationPropertyValue
            OrganizationID OrganizationProperty Pedigree RecordID RecordLevel
            RecordProperty RecordPropertyValue RecordSource ResourceID
            ResourceDatasetEdge ResourceOrganizationEdge
            ResourcePropertyValue ResourceProperty ResourceRelationship Taxon]
           [org.apache.thrift TBase TUnion]
           [clojure.lang Reflector]))

(def PAIL-PATH
  (let [desired-pail-name "vn-test"
        fn-check "test" ;; file exists in resources directory
        fn-check-path (.getPath (io/resource fn-check)) ;; get full path
        resources-path (apply str (drop-last (count fn-check) fn-check-path))]
    (str resources-path desired-pail-name)))

(def TEST-RECORDS
  [{:id "myid" :scientificname "Puma Concolor" :country "United States" :collectioncode "MVZ" :clazz "Mammalia"}
   {:id "myid" :scientificname "Puma Concolor" :country "United States" :collectioncode "MVZ" :clazz "Mammalia"}
   {:id "yourid" :scientificname "Mustache Mustachus" :country "Mustandia" :collectioncode "VIZZ" :clazz "Mustachia"}])

(def TEST-ORGS
  [{:key "MVZguid" :name "Museum of Vertebrate Zoology"}
   {:key "AMNHguid" :name "American Museum of Natural History"}])

(defn sink-test-records [pail-path record-data org-data]
  (let [record-guid "myguid"
        record-data (map (partial t/record-data record-guid) record-data)
        org-data (map t/organization-data org-data)]
    (for [d (concat record-data org-data)]
              (p/to-pail pail-path (<- [?d] (d ?d))))))

(defn mk-test-data
  [path]
  (sink-test-records path TEST-RECORDS TEST-ORGS))

(defn query-runner
  [fun k pail-path]
  (let [tap (p/split-chunk-tap pail-path (k stats-paths))]
    (fun tap)))

(defn get-test-record
  [v]
  (let [tap (p/split-chunk-tap PAIL-PATH v)]
    (ffirst (??<- [?obj]
                  (tap _ ?obj)))))

(tabular
 (fact
   "Checks unpack-RecordProperty"
   (let [rec (get-test-record ["prop" "RecordProperty" ?class-name])]
     (type (unpack-RecordProperty rec))) => ?class)
 ?class-name ?class
 "GeologicalContext" gulo.schema.GeologicalContext
 "Identification" gulo.schema.Identification
 "Location" gulo.schema.Location
 "MeasurementOrFact" gulo.schema.MeasurementOrFact
 "Occurrence" gulo.schema.Occurrence
 "RecordLevel" gulo.schema.RecordLevel
 "Taxon" gulo.schema.Taxon)

(tabular
 (fact
   "Checks get-RecordProperty-id"
   (let [rec (get-test-record ["prop" "RecordProperty" ?class-name])]
     (or (= "myid" (get-RecordProperty-id rec))
         (= "yourid" (get-RecordProperty-id rec)))) => true)
 ?class-name
 "GeologicalContext"
 "Identification"
 "Location"
 "MeasurementOrFact"
 "Occurrence"
 "RecordLevel"
 "Taxon")

(fact "Checks unpack-OrganizationProperty"
  (let [rec (get-test-record ["prop" "OrganizationProperty"])]
    (type (unpack-OrganizationProperty rec)) => gulo.schema.OrganizationProperty))

(fact "Checks get-OrganizationProperty-id"
  (let [rec (get-test-record ["prop" "OrganizationProperty"])]
    (or (= "MVZguid" (get-OrganizationProperty-id rec))
        (= "AMNHguid" (get-OrganizationProperty-id rec)))) => true)

(fact "Checks get-country"
  (let [rec (get-test-record ["prop" "RecordProperty" "Location"])]
    (or (= "United States" (get-country rec))
        (= "Mustandia" (get-country rec))) => true)

  ;; doesn't work for non-Location classes
  (let [rec (get-test-record ["prop" "RecordProperty" "Event"])]
    (get-country rec)) => (throws java.lang.IllegalArgumentException))

(fact "Checks get-scientific-name"
  (let [rec (get-test-record ["prop" "RecordProperty" "Taxon"])]
    (or (= "Mustache Mustachus" (get-scientific-name rec))
        (= "Puma Concolor" (get-scientific-name rec))) => true)

  ;; doesn't work for non-Taxon classes
  (let [rec (get-test-record ["prop" "RecordProperty" "Event"])]
    (get-scientific-name rec)) => (throws java.lang.IllegalArgumentException))

(fact "Checks get-collection-code"
  (let [rec (get-test-record ["prop" "RecordProperty" "RecordLevel"])]
    (or (= "MVZ" (get-collection-code rec))
        (= "VIZZ" (get-collection-code rec))) => true)

  ;; doesn't work for non-RecordLevel classes
  (let [rec (get-test-record ["prop" "RecordProperty" "Event"])]
    (get-collection-code rec)) => (throws java.lang.IllegalArgumentException))

(fact "Checks get-class"
  (let [rec (get-test-record ["prop" "RecordProperty" "Taxon"])]
    (or (= "Mammalia" (get-class rec))
        (= "Mustachia" (get-class rec))) => true)

  ;; doesn't work for non-Taxon classes
  (let [rec (get-test-record ["prop" "RecordProperty" "Event"])]
    (get-class rec)) => (throws java.lang.IllegalArgumentException))

(fact "Checks get-org-id"
  (let [rec (get-test-record ["prop" "OrganizationProperty"])]
    (get-org-id rec)) => "AMNHguid")

(fact "Checks get-unique-sci-names"
  (let [src (p/split-chunk-tap PAIL-PATH ["prop" "RecordProperty" "Taxon"])]
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
