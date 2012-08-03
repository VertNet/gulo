(ns gulo.main
  "This namespace provides entry points for harvesting, shredding, and preparing
  data from Darwin Core Archives into CartoDB."
  (:use [cascalog.api]
        [cascalog.more-taps :as taps :only (hfs-delimited)]        
        [gulo.core]
        [gulo.cdb :only (prepare-tables, wire-tables)]
        [gulo.harvest]
        [gulo.util :as util]
        [clojure.java.io :as io]))

(defn Harvest
  [urls harvest-path]  
  (let [csv-file (str harvest-path "/" "dwc.csv")]
    (io/delete-file csv-file true)
    (harvest urls csv-file)))

(defmain Shred
  [harvest-path hfs-path tables-path]
  (let [csv-file (str harvest-path "/dwc.csv")
        hfs-tax (str hfs-path "/tax")
        hfs-loc (str hfs-path "/loc")
        hfs-tax-loc (str hfs-path "/tax-loc")
        hfs-occ (str hfs-path "/occ")]
    (location-table (hfs-textline csv-file) hfs-loc)
    (taxon-table (hfs-textline csv-file) hfs-tax)
    (tax-loc-table csv-file hfs-tax hfs-loc hfs-tax-loc)
    (occ-table csv-file hfs-tax hfs-loc hfs-tax-loc hfs-occ)))

(defn PrepareTables
  []
  (prepare-tables))

(defn WireTables
  []
  (wire-tables))

