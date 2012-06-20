(ns gulo.core
  "This namespace downloads and harvests a set of Darwin Core Archives using
  Cascalog and unicorn magic."  
  (:use [gulo.util :as util :only (latlon-valid? gen-uuid)]
        [cascalog.api]
        [cascalog.more-taps :as taps :only (hfs-delimited)]
        [dwca.core :as dwca])
  (:import [org.gbif.dwc.record DarwinCoreRecord]))

(defn occ-tax-loc
  "Build occ and tax_loc tables."
  [occ-path tax-path loc-path occ-sink-path tax-loc-sink-path]
  (let [occ-tab (name-vars (taps/hfs-delimited occ-path) util/rec-fields)
        tax (taps/hfs-delimited tax-path)
        loc (taps/hfs-delimited loc-path)
        fields util/rec-fields
        occ-sink (hfs-textline occ-sink-path :sinkmode :replace)
        tax-loc-sink (taps/hfs-delimited tax-loc-sink-path :sinkmode :replace)
        tax-loc-occ (<- [?taxon-id ?loc-id ?occ-id]
                        (tax ?taxon-id ?name)
                        (loc ?loc-id ?lat ?lon)
                        (occ-tab :#> 183 {0 ?occ-id 22 ?lat 23 ?lon 160 ?name}))        
        tax-loc (<- [?taxon-id ?loc-id]
                    (tax-loc-occ ?taxon-id ?loc-id ?occ-id))
        tax-loc-uuid (<- [?uuid ?taxon-id ?loc-id]
                         (tax-loc ?taxon-id ?loc-id)
                         (util/gen-uuid :> ?uuid))]
    (?<- occ-sink
         (vec (cons "?tax-loc-uuid" fields))
         (tax-loc-occ ?taxon-id ?loc-id ?occ-id)
         (tax-loc-uuid ?tax-loc-uuid ?taxon-id ?loc-id)
         (occ-tab :>> fields))
    (?<- tax-loc-sink
         [?tax-loc-id ?taxon-id ?loc-id]
         (tax-loc-uuid ?tax-loc-id ?taxon-id ?loc-id))))

(defmapcatop map-names
  "Emits all taxon names."
  [kingdom phylum class order family genus species sciname]
  (vec (map vector [kingdom phylum class order family genus species sciname])))

(deffilterop valid-name?
  "Return true if name is valid, otherwise return false."
  [name]
  (not= name "_"))

(defn taxon-table
  "Create taxon table of unique names with generated UUIDs."
  [source sink-path]
  (let [sink (taps/hfs-delimited sink-path :sinkmode :replace)
        uniques (<- [?name]
                    (source :#> 183 {151 ?kingdom 152 ?phylum 153 ?class
                                     154 ?order 155 ?family 156 ?genus
                                     157 ?species 160 ?sciname})        
                    (map-names ?kingdom ?phylum ?class ?order ?family ?genus
                               ?species ?sciname :> ?name)
                    (valid-name? ?name))]
    (?<- sink
         [?uuid ?name]
         (uniques ?name)
         (util/gen-uuid :> ?uuid))))

(defn location-table
  "Create location table of unique and valid lat/lon with generated UUIDs from
  the supplied hfs-delimited source of DarwinCoreRecord values."
  [source sink-path]
  (let [sink (taps/hfs-delimited sink-path :sinkmode :replace)
        uniques (<- [?lat ?lon]
                    (source :#> 183 {22 ?lat 23 ?lon})
                    (util/latlon-valid? ?lat ?lon))]
    (?<- sink
         [?uuid ?lat ?lon]
         (uniques ?lat ?lon)
         (util/gen-uuid :> ?uuid))))

(comment
  ;; First harvest the archives:
  (harvest ["http://vertnet.nhm.ku.edu:8080/ipt/archive.do?r=nysm_mammals"]
           "/mnt/hgfs/Data/vertnet/gulo/harvest/data.csv")

  ;; Then MapReduce to build the tables:
  (location-table (taps/hfs-delimited "/mnt/hgfs/Data/vertnet/gulo/harvest/data.csv")
                  "/mnt/hgfs/Data/vertnet/gulo/hfs/loc")

  (taxon-table (taps/hfs-delimited "/mnt/hgfs/Data/vertnet/gulo/harvest/data.csv")
               "/mnt/hgfs/Data/vertnet/gulo/hfs/tax")

  (occ-tax-loc
   "/mnt/hgfs/Data/vertnet/gulo/harvest/data.csv"
   "/mnt/hgfs/Data/vertnet/gulo/hfs/tax/part-00000"
   "/mnt/hgfs/Data/vertnet/gulo/hfs/loc/part-00000"
   "/mnt/hgfs/Data/vertnet/gulo/hfs/occ"
   "/mnt/hgfs/Data/vertnet/gulo/hfs/tax-loc"))
