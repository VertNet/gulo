(ns gulo.views-test
  (:use gulo.views :reload)
  (:use [cascalog.api]
        [gulo.thrift :as t]
        [gulo.hadoop.pail :as p]
        [midje sweet cascalog])
  (:require [cascalog.ops :as c])
  (:import [backtype.hadoop.pail Pail]
           [gulo.schema Pedigree]))

(def PAIL-PATH "/tmp/vn-test-tmp")

(def TEST-RECORDS
  [{:id "myid" :scientificname "Puma Concolor" :country "United States" :collectioncode "MVZ" :clazz "Mammalia"}
   {:id "myid2" :scientificname "Puma Concolor" :country "United States" :collectioncode "MVZ" :clazz "Mammalia"}
   {:id "yourid" :scientificname "Mustache Mustachus" :country "Mustandia" :collectioncode "VIZZ" :clazz "Mustachia"}])

(defn sink-test-records [pail-path records]
  (let [dataset-guid "myguid"
        record-data (map (partial t/record-data dataset-guid) records)]
    (p/to-pail pail-path (vec (map vector (flatten record-data))))))

(defn delete-directory
  [path]
  (let [fs (file-seq (clojure.java.io/file path))]
    (for [f (reverse fs)]
      (clojure.java.io/delete-file f))))

(defn query-runner
  [fun k pail-path]
  (let [tap (p/split-chunk-tap pail-path (k stats-paths))]
    (fun tap)))

(def test-data
  (sink-test-records PAIL-PATH TEST-RECORDS))

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

(fact "Checks `get-most-recent-fields`."
  (let [dataset-guid "myguid"
        data (first (nth (first (map (partial t/record-data dataset-guid) TEST-RECORDS)) 2))
        time (first (t/unpack (first (t/unpack data))))
        data1 (-> data (.setPedigree (Pedigree. 1)))
        time1 (first (t/unpack (first (t/unpack data))))]
    (get-most-recent-fields [[time data] [time1 data1]]))
  => [[[nil nil nil "United States" nil nil nil nil nil nil nil nil nil nil nil
        nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil
        nil nil nil nil nil nil nil nil nil nil nil]]])

(fact "Checks `keep-most-recent` query."
  (let [dataset-guid "myguid"
        src (first (map (partial t/record-data dataset-guid) TEST-RECORDS))
        src (flatten (concat src src))
        src (map vector (repeat (count src) "") src)
        output (keep-most-recent src)]
    ;; check an actual output tuple
    output
    => (produces-some [["myid" "class gulo.schema.Event"
                        [nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil]]])
    ;; check number of output records - should be 9, corresponding to
    ;; different RecordProperty types (e.g. Taxon, Event, etc.)
    (count (first (??- output))) => 9))

(fact "Check `concat-fields`"
  (concat-fields [[1 2 3] [4 5 6]]) => [[[1 2 3 4 5 6]]])

(fact "Checks `concat-fields-query`.

       Note that this test depends on the records in the pail, which
       in this simple case all have the same Pedigree. The test for
       `keep-most-recent` checks that only the most recent records are
       retained."
  (let [output (??- (concat-fields-query PAIL-PATH ["prop" "RecordProperty"]))]
    (count (last (ffirst output)))) => 156)
