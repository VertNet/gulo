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
  [harvest-path hfs-path]
  (let [tax-out (str hfs-path "/tax")
        loc-out (str hfs-path "/loc")
        tax-loc-out (str hfs-path "/taxloc")
        occ-out (str hfs-path "/occ")]
    (location-table (hfs-textline harvest-path)
                    (hfs-textline loc-out :sinkmode :replace))
    (taxon-table (hfs-textline harvest-path)
                 (hfs-textline tax-out :sinkmode :replace))
    (tax-loc-table (hfs-textline harvest-path)
                   (hfs-textline tax-out)
                   (hfs-textline loc-out)
                   (hfs-textline tax-loc-out :sinkmode :replace))
    (occ-table (hfs-textline harvest-path)
               (hfs-textline tax-out)
               (hfs-textline loc-out)
               (hfs-textline tax-loc-out)
               (hfs-textline occ-out :sinkmode :replace))))

(defn PrepareTables
  "Prepare table files for upload to CartoDB."
  []
  (prepare-tables))

(defn WireTables
  "Wire CartoDB tables by adding indexes and deleting unused columns."
  []
  (wire-tables))

