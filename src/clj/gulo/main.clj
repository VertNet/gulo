(ns gulo.main
  (:use [cascalog.api]
        [cascalog.more-taps :as taps :only (hfs-delimited)]        
        [gulo.cdb]
        [gulo.core]
        [gulo.harvest]))

(defmain Shred
  [urls harvest-path hfs-path tables-path]
  (let [csv-file (str harvest-path "/" "dwc.csv")
        hfs-tax (str hfs-path "/" "tax")
        tax-part (str hfs-tax "/part-0000")
        hfs-loc (str hfs-path "/" "loc")
        loc-part (str hfs-loc "/part-0000")
        hfs-tax-loc (str hfs-path "/" "tax-loc")
        tax-loc-part (str hfs-tax-loc "/part-0000")
        hfs-occ (str hfs-path "/" "occ")]
    (harvest urls csv-file)
    (location-table (taps/hfs-delimited csv-file) hfs-loc)
    (taxon-table (taps/hfs-delimited csv-file ) hfs-tax)
    (tax-loc-table csv-file tax-part loc-part hfs-tax-loc)
    (occ-table csv-file tax-part loc-part tax-loc-part hfs-occ)
    (prepare-tables)))

(defmain Burp [] (wire-tables))  

