(ns gulo.main
  "This namespace provides entry points for harvesting, shredding, and preparing
  data from Darwin Core Archives into CartoDB."
  (:use [cascalog.api]
        [cascalog.more-taps :as taps :only (hfs-delimited)]
        [gulo.views]
        [gulo.harvest :only (harvest-all)]
        [gulo.cdb :only (prepare-tables, wire-tables)]
        [clojure.java.io :as io])
  (:require [gulo.util :as util :only (mk-stats-out-path todays-date)]))

(defn Harvest
  "Harvest supplied publishers to a CSV file at supplied path. Publishers is a
  sequence of maps containing :dwca_url, :inst_code, and :inst_name keys."
  [publishers path]  
  (let [csv-file (str path "/" "dwc.csv")]
    (io/delete-file csv-file true)
    (harvest-all publishers csv-file)))

(defn PrepareTables
  "Prepare table files for upload to CartoDB."
  []
  (prepare-tables))

(defn WireTables
  "Wire CartoDB tables by adding indexes and deleting unused columns."
  []
  (wire-tables))

(defn run-stat-query
  "Run a specific statistic query."
  [query in-path base-path query-name]
  (let [final-path (util/mk-stats-out-path base-path (util/todays-date) query-name)]
    (?- (hfs-textline final-path) (query (hfs-textline in-path)))))

(defmain RunStats
  "Run all statistics queries."
  [in-path out-dir]
  (let [today (util/todays-date)
        queries-names [[taxa-count "taxa-count"]
                      [publisher-count "publisher-count"]
                      [total-recs "total-recs"]
                      [total-recs-by-country "total-recs-by-country"]
                      [total-recs-by-collection "total-recs-by-collection"]
                      [total-recs-by-class "total-recs-by-class"]]]
    (for [[q q-name] queries-names]
      (run-stat-query q in-path out-dir q-name))))
