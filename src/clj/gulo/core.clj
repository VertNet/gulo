(ns gulo.core
  "This namespace downloads and harvests a set of Darwin Core Archives using
  Cascalog and unicorn magic."  
  (:use [gulo.util :as util :only (latlon-valid? gen-uuid)]
        [cascalog.api]
        [clojure.contrib.string :as s :only (grep)]
        [cascalog.more-taps :as taps :only (hfs-delimited)]
        [dwca.core :as dwca]
        [cartodb.client :as cdb :only (query)]
        [clojure.string :only (join split lower-case)]))

;; ([?kingdom ?phylum ?class ?order ?family ?genus ?species ?sciname]
;;    (source :#> 183 {151 ?kingdom 152 ?phylum 153 ?class 154 ?order
;;                     155 ?family 156 ?genus 157 ?species 160 ?sciname}))]

(defn taxon-location-table
  "Create taxon location table."
  [taxon location occurrence sink-path]
  (let [sink (taps/hfs-delimited sink-path :sinkmode :replace)]
    (?<- sink
         [?taxon-id ?loc-id ?occ-id]
         (taxon ?taxon-id ?name)
         (location ?loc-id ?lat ?lon)
         (occurrence :#> 183 {0 ?occ-id 22 ?lat 23 ?lon 160 ?name}))))

(defmapcatop explode-names
  "Emits all taxon names."
  [kingdom phylum class order family genus species sciname]
  (vec (map vector [kingdom phylum class order family genus species sciname])))

(defn taxon-table
  "Create taxon table of unique names with generated UUIDs."
  [source sink-path]
  (let [sink (taps/hfs-delimited sink-path :sinkmode :replace)
        unique-names (<- [?name]
                         (source :#> 183 {151 ?kingdom 152 ?phylum 153 ?class
                                          154 ?order 155 ?family 156 ?genus
                                          157 ?species 160 ?sciname})        
                         (explode-names ?kingdom ?phylum ?class ?order ?family
                                        ?genus ?species ?sciname :> ?name))]
    (?<- sink
         [?uuid ?name]
         (unique-names ?name)
         (util/gen-uuid :> ?uuid))))

(defn location-table
  "Create location table of unique and valid lat/lon with generated UUIDs."
  [source sink-path]
  (let [sink (taps/hfs-delimited sink-path :sinkmode :replace)
        unique-latlons (<- [?lat ?lon]
                           (source :#> 183 {22 ?lat 23 ?lon})
                           (util/latlon-valid? ?lat ?lon))]
    (?<- sink
         [?uuid ?lat ?lon]
         (unique-latlons ?lat ?lon)
         (util/gen-uuid :> ?uuid))))

(defn explode
  [rec]
  (vec (cons (util/gen-uuid) (field-vals rec))))

(defmapcatop explode-lines
  "Emit records as tab delineated lines from archive located at URL. A UUID is
  prepended to each line for use by Cascalog joins when building other tables."
  [url]
  (vec (map explode (dwca/open url))))

(defn occurrence-table
  "Download and store records from many Darwin Core Archive URLs to CSV file."
  [source sink-path]
  (let [sink (taps/hfs-delimited sink-path :sinkmode :replace)]
    (?<- sink
         [?0 ?1 ?2 ?3 ?4 ?5 ?6 ?7 ?8 ?9 ?10 ?11 ?12 ?13 ?14 ?15 ?16 ?17 ?18 ?19
          ?20 ?21 ?22 ?23 ?24 ?25 ?26 ?27 ?28 ?29 ?30 ?31 ?32 ?33 ?34 ?35 ?36
          ?37 ?38 ?39 ?40 ?41 ?42 ?43 ?44 ?45 ?46 ?47 ?48 ?49 ?50 ?51 ?52 ?53
          ?54 ?55 ?56 ?57 ?58 ?59 ?60 ?61 ?62 ?63 ?64 ?65 ?66 ?67 ?68 ?69 ?70
          ?71 ?72 ?73 ?74 ?75 ?76 ?77 ?78 ?79 ?80 ?81 ?82 ?83 ?84 ?85 ?86 ?87
          ?88 ?89 ?90 ?91 ?92 ?93 ?94 ?95 ?96 ?97 ?98 ?99 ?100 ?101 ?102 ?103
          ?104 ?105 ?106 ?107 ?108 ?109 ?110 ?111 ?112 ?113 ?114 ?115 ?116 ?117
          ?118 ?119 ?120 ?121 ?122 ?123 ?124 ?125 ?126 ?127 ?128 ?129 ?130 ?131
          ?132 ?133 ?134 ?135 ?136 ?137 ?138 ?139 ?140 ?141 ?142 ?143 ?144 ?145
          ?146 ?147 ?148 ?149 ?150 ?151 ?152 ?153 ?154 ?155 ?156 ?157 ?158 ?159
          ?160 ?161 ?162 ?163 ?164 ?165 ?166 ?167 ?168 ?169 ?170 ?171 ?172 ?173
          ?174 ?175 ?176 ?177 ?178 ?179 ?180 ?181 ?182]       
         (source ?url)
         (explode-lines ?url :> ?0 ?1 ?2 ?3 ?4 ?5 ?6 ?7 ?8 ?9 ?10 ?11 ?12 ?13
                        ?14 ?15 ?16 ?17 ?18 ?19 ?20 ?21 ?22 ?23 ?24 ?25 ?26 ?27
                        ?28 ?29 ?30 ?31 ?32 ?33 ?34 ?35 ?36 ?37 ?38 ?39 ?40 ?41
                        ?42 ?43 ?44 ?45 ?46 ?47 ?48 ?49 ?50 ?51 ?52 ?53 ?54 ?55
                        ?56 ?57 ?58 ?59 ?60 ?61 ?62 ?63 ?64 ?65 ?66 ?67 ?68 ?69
                        ?70 ?71 ?72 ?73 ?74 ?75 ?76 ?77 ?78 ?79 ?80 ?81 ?82 ?83
                        ?84 ?85 ?86 ?87 ?88 ?89 ?90 ?91 ?92 ?93 ?94 ?95 ?96 ?97
                        ?98 ?99 ?100 ?101 ?102 ?103 ?104 ?105 ?106 ?107 ?108 ?109
                        ?110 ?111 ?112 ?113 ?114 ?115 ?116 ?117 ?118 ?119 ?120
                        ?121 ?122 ?123 ?124 ?125 ?126 ?127 ?128 ?129 ?130 ?131
                        ?132 ?133 ?134 ?135 ?136 ?137 ?138 ?139 ?140 ?141 ?142
                        ?143 ?144 ?145 ?146 ?147 ?148 ?149 ?150 ?151 ?152 ?153
                        ?154 ?155 ?156 ?157 ?158 ?159 ?160 ?161 ?162 ?163 ?164
                        ?165 ?166 ?167 ?168 ?169 ?170 ?171 ?172 ?173 ?174 ?175
                        ?176 ?177 ?178 ?179 ?180 ?181 ?182))))

(defn harvest
  [source occ-path loc-path taxon-path taxon-loc-path ]
  (occurrence-table source occ-path)
  (let [occ-source (taps/hfs-delimited occ-path :sinkmode :replace)]
    (location-table occ-source loc-path)
    (taxon-table occ-source taxon-path)
    (taxon-location-table (taps/hfs-delimited taxon-path :sinkmode :replace)
                          (taps/hfs-delimited loc-path :sinkmode :replace)
                          occ-source taxon-loc-path)))
