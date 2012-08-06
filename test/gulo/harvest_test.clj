(ns gulo.harvest-test
  "Unit test the gulo.harvest namespace."
  (:use gulo.harvest
        [midje sweet]))

(def uuid-pattern #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
(def uuid-pattern-str "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")

(fact "Test prepend-uuid function."
  (let [vals ["gulo" "shreds"]
        vals (prepend-uuid vals)]
    (count vals) => 3
    (nth vals 1) => "gulo"
    (nth vals 2) => "shreds"
    (.matches (re-matcher uuid-pattern (first vals))) => true))

(fact "Test append-vals function."
  (let [vals ["gulo" "shreds"]
        vals (append-vals vals "name" "code")]
    (count vals) => 4
    (nth vals 0) => "gulo"
    (nth vals 1) => "shreds"
    (nth vals 2) => "name"
    (nth vals 3) => "code"))

(fact "Test dwca-urls function."
  (let [row (first (dwca-urls))]
    (contains? row :dwca_url) => true
    (contains? row :inst_name) => true
    (contains? row :inst_code) => true))

