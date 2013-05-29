(ns gulo.harvest-test
  "Unit test the gulo.harvest namespace."
  (:use gulo.harvest
        [midje sweet])
  (:require [gulo.util :as util]
            [gulo.fields :as f]))

(def uuid-pattern #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
(def uuid-pattern-str "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")

(fact "Test prepend-uuid function."
  (let [vals ["gulo" "shreds"]
        vals (prepend-uuid vals)]
    (count vals) => 3
    (nth vals 1) => "gulo"
    (nth vals 2) => "shreds"
    (.matches (re-matcher uuid-pattern (first vals))) => true))

(fact "Test `get-resource-props`"
  (let [val-vec (into ["a" "a\nb"] (repeat (count f/resource-fields) "a"))
        resource-map (zipmap f/resource-fields val-vec)]
    (get-resource-props resource-map)) => ["a" "a b" "a" "a" "a" "a" "a" "a" "a" "a" "a" "a"])

(fact "Test `get-count`."
  (get-count "http://ipt.vertnet.org:8080/ipt/resource.do?r=ttrs_birds")
  => "3945")

(fact "Test `resource-row`."
  (let [row-map (resource-row
                 "http://ipt.vertnet.org:8080/ipt/resource.do?r=ttrs_birds"
                 "TTRS" true)]
    (:ipt row-map) => true
    (:icode row-map) => "TTRS"
    (:count row-map) => "3945"
    (:orgname row-map) => "Tall Timbers Research Station and Land Conservancy"
    (:title row-map) => "TTRS Ornithology"))

(fact "Test `query-resource-rows`."
  (let [rows (query-resource-rows)]
    (set (keys (first rows)))) => (set [:url :icode :ipt]))

(fact "Test `resource-staging-rows` parsing. Use a predefined row to avoid extra
       roundtrip to & dl from CartoDB. Actual content of row is tested in
       test for `resource-row."
  (let [rows [{:pubdate "Mon Apr 16 00:00:00 PDT 2012", :ipt true, :eml
               "http://ipt.vertnet.org:8080/ipt/eml.do?r=ttrs_birds",
               :count "3945",
               :dwca "http://ipt.vertnet.org:8080/ipt/archive.do?r=ttrs_birds",
               :title "TTRS Ornithology", :icode "TTRS",
               :rights "Tall Timbers data is governed by the Creative
               Commons Attribution 3.0 license
               (http://creativecommons.org/licenses/by/3.0/legalcode). Any
               use of data or images must be attributed to Tall
               Timbers Research Station and Land Conservancy.",
               :url "http://ipt.vertnet.org:8080/ipt/resource.do?r=ttrs_birds",
               :orgname "Tall Timbers Research Station and Land Conservancy",
               :email "jim@ttrs.org", :contact "Jim Cox",
               :description "Scientists, natural history students,
               artists, and others interested in the fauna and flora
               of the southeast are encouraged to visit The Natural
               History Museum and Scientific Collections at Tall
               Timbers. Special arrangements must be made to view and
               work with specimens, and the general hours of operation
               are 8:30 AM-4:30 PM. Loans are generally discouraged
               but are allowed occasionally and governed by formal
               agreements developed on an individual basis."}]]
    (set (keys (first (resource-staging-rows rows)))))
  => (set [:pubdate :ipt :eml :count :dwca :title :icode :emlrights :url :orgname
           :email :contact :description]))
