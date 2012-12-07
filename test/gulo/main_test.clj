(ns gulo.main-test
  "Integration testing"
  (:use gulo.main
        [gulo.core :only (MIN-OBS)]
        cascalog.api
        [midje cascalog sweet]))


(def TMP "/tmp/occ")

(def sample-occ-data
  [["Passer domesticus" "999999999" "-40.8747" "170.851" "10" "2007" "6" "4"]])

(defn big-sample-data
  "Make non-repeating sample data based on sample-occ-data."
  [n]
  (let [ids (range (inc n))
        base-obs (first sample-occ-data)
        [head tail] [(first base-obs) (rest (rest base-obs))]]
    (into (vec (map #(into (vector head (str %)) tail) ids))
          [["Really big ants" "99999999" "-40.8747" "170.851" "10" "2007" "6" "4"]])))

(defn sink-fake-data
  []
  (?- (hfs-seqfile TMP :sinkmode :replace) (big-sample-data MIN-OBS)))

(fact
  "Check that Shredding runs through."
  (let [out-loc "/tmp/output"]
    (sink-fake-data)
    (Shred TMP out-loc)) => nil)
