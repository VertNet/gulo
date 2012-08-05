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
  "Harvest supplied publishers to a CSV file at supplied path. Publishers is a
  sequence of maps containing :dwca_url, :inst_code, and :inst_name keys."
  [publishers path]  
  (let [csv-file (str path "/" "dwc.csv")]
    (io/delete-file csv-file true)
    (harvest publishers csv-file)))

(defmain Shred
  "Shred a CSV file containing Darwin Core records into the VertNet schema."
  [harvest-path hfs-path tables-path]
  (let [csv-file (str harvest-path "/dwc.csv")
        hfs-tax (str hfs-path "/tax")
        hfs-loc (str hfs-path "/loc")
        hfs-tax-loc (str hfs-path "/taxloc")
        hfs-occ (str hfs-path "/occ")]
    (location-table (hfs-textline csv-file) hfs-loc)
    (taxon-table (hfs-textline csv-file) hfs-tax)
    (tax-loc-table csv-file hfs-tax hfs-loc hfs-tax-loc)
    (occ-table csv-file hfs-tax hfs-loc hfs-tax-loc hfs-occ)))

(defn PrepareTables
  "Prepare table files for upload to CartoDB."
  []
  (prepare-tables))

(defn WireTables
  "Wire CartoDB tables by adding indexes and deleting unused columns."
  []
  (wire-tables))

