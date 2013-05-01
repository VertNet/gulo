(ns gulo.harvest-test
  "Unit test the gulo.harvest namespace."
  (:use gulo.harvest
        [midje sweet])
  (:require [gulo.util :as util]))

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
  (let [val-vec ["a" "a\nb" "a" "a" "a" "a" "a" "a" "a" "a" "a" "a"]
        resource-map (zipmap util/resource-fields val-vec)]
    (get-resource-props resource-map)) => ["a" "a b" "a" "a" "a" "a" "a" "a" "a" "a" "a" "a"])
