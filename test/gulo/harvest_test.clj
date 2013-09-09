(ns gulo.harvest-test
  "Unit test the gulo.harvest namespace."
  (:use gulo.harvest
        [midje sweet])
  (:require [gulo.util :as util]
            [gulo.fields :as f]))

(def uuid-pattern #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
(def uuid-pattern-str "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")

(fact "Test `prepend-uuid`."
  (let [vals ["gulo" "shreds"]
        vals (prepend-uuid vals)]
    (count vals) => 3
    (nth vals 1) => "gulo"
    (nth vals 2) => "shreds"
    (.matches (re-matcher uuid-pattern (first vals))) => true))

(future-fact "Test `file->s3`.")
(future-fact "Test `file->files`.")

(future-fact "Test `get-season`.")
(future-fact "Test `prep-record`.")

(fact "Test `get-resource-props` by getting count from result (element 11)."
  (let [url "http://ipt.vertnet.org:8080/ipt/resource.do?r=mvz_egg"]
    (nth (get-resource-props url) 11)) => "14001")

(fact "Test `gen-local-csv-path`."
  (gen-local-csv-path "http://ipt.vertnet.org:8080/ipt/resource.do?r=mvz_egg"
                      "/tmp/test-data" "2013-01-01" "23ksdkjlwkek")
  => "/tmp/test-data/2013-01-01/mvz_egg-23ksdkjlwkek/mvz_egg-23ksdkjlwkek.csv")

(fact "Test `url->ipt`."
  (url->ipt "http://ipt.vertnet.org:8080/ipt/resource.do?r=mlz_bird") => true)

(fact "Test `url->icode`."
  (url->icode "http://ipt.vertnet.org:8080/ipt/resource.do?r=mlz_bird") => "MLZ")

(fact "Test `url->networks`."
  (url->networks "http://ipt.vertnet.org:8080/ipt/resource.do?r=mvz_egg") => "ORNIS")

(fact "Test `url->coll-count`."
  (url->coll-count "http://ipt.vertnet.org:8080/ipt/resource.do?r=mlz_bird") => nil)

(future-fact "Test `fetch-url`.")

(fact "Test `get-count`."
  (get-count "http://ipt.vertnet.org:8080/ipt/resource.do?r=ttrs_birds")
  => "3945"
  (get-count "http://ipt.calacademy.org:8080/ipt/resource.do?r=herp")
  => "-1"
  (get-count "http://ipt.nhm.ku.edu/ipt/resource.do?r=kubi_herps")
  => "-1")

(future-fact "Test `get-citation`")

(fact "Test `mk-resource-map`."
  (let [row-map (mk-resource-map
                 "http://ipt.vertnet.org:8080/ipt/resource.do?r=ubc_bbm_ctc_herps")]
    (:ipt row-map) => true
    (:icode row-map) => "UBCBBM"
    (:count row-map) => "1863"
    (:orgname row-map) => "University of British Columbia Beaty Biodiversity Museum"
    (:citation row-map) => "Cowan Tetrapod Collection at the University of British Columbia Beaty Biodiversity Museum (UBCBBM)"
    (:networks row-map) => "HerpNET"))

(future-fact "Test `execute-sql`.")

(fact "Test `get-resource-staging-urls."
  (count (get-resource-urls "resource_staging" :limit 3)) => 3
  (count (get-resource-urls "resource" :limit 3)) => 3)

(future-fact "Test `sync-resource`.")
(future-fact "Test `sync-resource-table`.")

(fact "Test `gen-gcs-path`."
  (gen-gcs-path "gs://mybucket/staging" "2013-01-01")
  => "gs://mybucket/staging/2013-01-01/")

(future-fact "Test `copy-to-gcs`.")

(fact "Test `path-split`."
  (path-split "/tmp/test-data/data.csv") => ["/tmp/test-data" "data.csv"])

(fact "Test `get-harvest-urls`."
  (let [path-coll ["http://ipt.vertnet.org:8080/ipt/resource.do?r=mvz_bird"
                   "http://ipt.vertnet.org:8080/ipt/resource.do?r=mvz_herp"]
        path-file "/tmp/vn-paths.txt"
        _ (spit path-file (clojure.string/join "\n" path-coll))]
    (get-harvest-urls :path-file path-file) => path-coll
    (get-harvest-urls :path-coll path-coll) => path-coll
    (< 2 (count (get-harvest-urls))) => true))

(fact "Test that `harvest-resource` completes successfully."
  (harvest-resource "http://ipt.vertnet.org:8080/ipt/resource.do?r=msbobs_mamm" "/tmp" "2013-01-01")
  1 => 1)
