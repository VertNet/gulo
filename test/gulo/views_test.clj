(ns gulo.views-test
  (:use gulo.views :reload)
  (:use [cascalog.api]
        [midje sweet cascalog])
  (:require [cascalog.ops :as c]
            [clojure.java.io :as io]))

(def src (hfs-textline (.getPath (io/resource "test-stats"))))

(fact "Check `uniques-query` with one field."
  (uniques-query src ["?scientificname"])
  => (produces-some [["Aplodontia rufa"] ["Blarina brevicauda"] ["Canis latrans"]
                     ["Castor canadensis"] ["Citellus tridecemlineatus"]
                     ["Clethrionomys gapperi"] ["Cryptotis parva"]
                     ["Dasypterus floridanus"] ["Dasypus novemcinctus"]
                     ["Didelphis marsupialis"]]))

(fact "Check `uniques-query` with multiple fields."
  (uniques-query src ["?scientificname" "?year"])
  => (produces-some [["Aplodontia rufa" "1974"] ["Blarina brevicauda" "1966"]
                     ["Blarina brevicauda" "1967"] ["Blarina brevicauda" "1968"]
                     ["Blarina brevicauda" "1971"] ["Blarina brevicauda" "1973"]
                     ["Blarina brevicauda" "1976"] ["Blarina brevicauda" "1977"]
                     ["Blarina brevicauda" "1978"] ["Canis latrans" "1987"]]))

(fact "Check `taxa-count`."
  (taxa-count src) => (produces [[63]]))

(fact "Check `publisher-count`."
  (publisher-count src) => (produces [[1]]))

(fact "Check `total-recs`."
  (total-recs src) => (produces [[968]]))

(fact "Check `total-recs-by-country`."
  (total-recs-by-country src) => (produces [["United States" 968]]))

(fact "Check `total-recs-by-collection`."
  (total-recs-by-collection src) => (produces [["Mammals" 968]]))

(fact "Check `total-recs-by-class`."
  (total-recs-by-class src) => (produces [["Mammalia" 968]]))
