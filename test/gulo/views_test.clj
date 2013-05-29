(ns gulo.views-test
  (:use gulo.views :reload)
  (:use [cascalog.api]
        [midje sweet cascalog])
  (:require [cascalog.ops :as c]
            [clojure.java.io :as io]))

(def src (hfs-textline (.getPath (io/resource "test-stats"))))

(fact "Check `uniques-query` with one field."
  (uniques-query src ["?scientificname"])
  => (produces-some [["Accipiter cooperii"] ["Accipiter striatus"]
                     ["Actitis macularius"] ["Aix sponsa"] ["Alectoris chukar"]
                     ["Anas clypeata"] ["Anas crecca"] ["Anas discors"]
                     ["Anas platyrhynchos"] ["Anas rubripes"]]))

(fact "Check `uniques-query` with multiple fields."
  (uniques-query src ["?scientificname" "?year"])
  => (produces-some [["Accipiter cooperii" "1931"] ["Accipiter cooperii" "1934"]
                     ["Accipiter cooperii" "1939"] ["Accipiter striatus" "1930"]
                     ["Accipiter striatus" "1932"] ["Accipiter striatus" "1933"]
                     ["Accipiter striatus" "1939"] ["Accipiter striatus" "1940"]
                     ["Accipiter striatus" "1953"] ["Accipiter striatus" "1959"]]))

(fact "Check `taxa-count`."
  (taxa-count src) => (produces [[132]]))

(fact "Check `publisher-count`."
  (publisher-count src) => (produces [[1]]))

(fact "Check `total-recs`."
  (total-recs src) => (produces [[500]]))

(fact "Check `total-recs-by-country`."
  (total-recs-by-country src) => (produces [["UNITED STATES" 499]
                                            ["MEXICO" 1]]))

(fact "Check `total-recs-by-collection`."
  (total-recs-by-collection src) => (produces [["Ornithology" 500]]))

(fact "Check `total-recs-by-class`."
  (total-recs-by-class src) => (produces [["Aves" 500]]))
