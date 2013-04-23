(ns gulo.views
  (:use [cascalog.api]
        [gulo.util :as u])
  (:require [cascalog.ops :as c]))

(defn uniques-query
  "Given a source of harvested textlines and a vector of fields to extract,
   return supplied fields for unique records.

   Usage:
     (let [src (hfs-textline \"resources/test-stats/\")]
       (??- (uniques-query src [\"?collectioncode\" \"?catalognumber\"])))
     ;=> (([\"Mammals\" \"1\"] [\"Mammals\" \"2\"]))

     (let [src (hfs-textline \"resources/test-stats/\")]
       (??- (uniques-query src [\"?collectioncode\"])))
     ;=> (([\"Mammals\"]))"
  [textline-src fields-vec]
  (<- fields-vec
      (textline-src ?line)
      (u/splitline ?line :>> u/harvest-fields)
      (:distinct true)))

(defn taxa-count
  "Count unique taxa."
  [src]
  (let [uniques (uniques-query src ["?scientificname"])]
    (<- [?count]
        (uniques ?scientificname)
        (c/count ?count))))

(defn publisher-count
  "Count unique publishers."
  [src]
  (let [uniques (uniques-query src ["?name"])] ;; correct publisher id?
    (<- [?count]
        (uniques ?dwca)
        (c/count ?count))))

(defn total-recs
  "Count unique occurrences."
  [src]
  (let [uniques (uniques-query src ["?id"])] ;; correct id?
    (<- [?count]
        (uniques ?occurrenceid)
        (c/count ?count))))

(defn total-recs-by-country
  "Count unique occurrence records by country."
  [src]
  (let [uniques (uniques-query src ["?country" "?id"])] ;; correct id?
    (<- [?country ?count]
        (uniques ?country _)
        (c/count ?count))))

(defn total-recs-by-collection
  "Count unique records by collection."
  [src]
  (let [uniques (uniques-query src ["?collectioncode" "?id"])]
    (<- [?collectioncode ?count]
        (uniques ?collectioncode _)
        (c/count ?count))))

(defn total-recs-by-class
  "Count unique records by class."
  [src]
  (let [uniques (uniques-query src ["?classs" "?id"])]
    (<- [?classs ?count]
        (uniques ?classs _)
        (c/count ?count))))
