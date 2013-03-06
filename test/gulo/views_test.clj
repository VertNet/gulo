(ns gulo.views-test
  (:use gulo.views :reload)
  (:use [cascalog.api]
        [gulo.thrift :as t]
        [gulo.hadoop.pail :as p])
  (:require [cascalog.ops :as c]
            [midje sweet cascalog])
  (:import [backtype.hadoop.pail Pail]))

;; needed fields

;; RecordProperty
;;;;;;;;;;;;;;;;;;;

;; ScientificName
;; SourceID
;; Country
;; CollectionCode
;; Clazz

;; OrganizationProperty
;;;;;;;;;;;;;;;;;;;;;;;;
;; Uuid


(defn get-n-sample-records
  "Grab n records from the sample pail."
  [n & [p]]
  (let [path (str "/tmp/vn/" p)]
    (take n (Pail. path))))

(comment
  ;; records by country
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:records-by-country stats-paths))]
    (??- (total-occ-by-country-query tap)))

  ;; count records
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:total-records stats-paths))]
    (??- (total-occurrences-query tap)))

  ;; total publishers
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:total-publishers stats-paths))]
    (??- (total-publishers-query tap)))

  ;; total taxa
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:total-taxa stats-paths))]
    (??- (taxa-count-query tap)))

  ;; records by collection
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:records-by-collection stats-paths))]
    (??- (total-by-collection-query tap)))

  ;; records by class
  (let [tap (p/split-chunk-tap "/tmp/vn/" (:records-by-class stats-paths))]
    (??- (total-by-class-query tap))))

(comment
  (defn test-data
    []
    (let [resource-url "http://ipt.vertnet.org:8080/ipt/resource.do?r=ttrs_mammals"
          sql (format "SELECT author,link,eml,dwca,orgcontact,orgname,orgurl,guid,description,pubdate,publisher,name FROM resources WHERE link = '%s'" resource-url)
          resource (first (:rows (cartodb/query sql "vertnet" :api-key api-key)))
          {:keys [resource dataset organization]} (resource->metadata resource)]
      (sink-metadata "/tmp/vn" resource dataset organization)
      (sink-data "/tmp/vn" resource))))
