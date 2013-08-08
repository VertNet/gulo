(ns gulo.harvest
  "Functions for harvesting Darwin Core Archives from IPT.

  A Darwin Core Archive (archive) is a zip file that contains a Darwin
  Core record dataset with an EML metadata document. The metadata
  describes what the dataset is and when it was last updated.

  Archives are published online by the Integrated Publishing
  Toolkit (IPT) as resources. Each resource has an RSS feed that
  describes the resource, when it was last published, and provides
  download links to the archive.

  For convienience, we store resource URLs that we want to harvest in
  CartoDB:

    https://vertnet.cartodb.com/tables/resource

  When we harvest resources, we extract the dataset and metadata from
  their archive, lookup the resource organization if possible, and
  encode everything (records, resource, dataset, organization) into a
  simple CSV textline that gets uploaded to S3.
  
  For reference:
    Darwin Core: http://goo.gl/HgvY4
    Darwin Core Archive: http://goo.gl/ee3KC
    EML: http://goo.gl/Z27H5
    IPT: http://goo.gl/GtJMF
    S3: http://goo.gl/ailE"
  (:use [cascalog.api]
        [cartodb.core :as cartodb]
        [cartodb.utils :as cartodb-utils]
        [clojure.data.csv :as csv]
        [net.cgrand.enlive-html :as html
         :only (html-resource, select, attr-values)])
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.contrib.io :as cio :only (delete-file-recursively)]
            [gulo.fields :as f]
            [gulo.util :as util]
            [dwca.core :as dwca])
  (:import [java.io File]
           [org.gbif.metadata.eml EmlFactory]))

(def STAGING-TABLE "resource_staging")
(def HARVEST-TABLE "resource")
(def GS-PATH "gs://vn-staging/data/")

(defn execute-sql
  ([sql]
     (execute-sql sql "vertnet"))
  ([sql account]
     (:rows (cartodb/query sql account :api-key util/api-key))))

(defn prepend-props
  [vals props]
  (concat props vals))

(defn prepend-uuid
  "Prepend UUID to sequence of vals."
  [vals]
  (cons (util/gen-uuid) vals))

(defn prep-record
  "Prepend record property fields to fields from a Darwin Core Archive record."
  [props record]
  (-> (dwca/field-vals record)
      prepend-uuid
      (prepend-props props)))

(defn get-resource-props
  "Extract and clean up props in resource map."
  [resource-url]
  (let [sql (format "SELECT * FROM resource WHERE url='%s' ORDER BY cartodb_id"
                    resource-url)
        [resource-map] (execute-sql sql)
        props (map #(% resource-map) f/resource-fields)]
    (map util/line-breaks->spaces (flatten props))))

(defn resource->csv
  "Convert Darwn Core records from supplied IPT resource URL to CSV"
  [url path date-str]
  (prn (format "Downloading records from %s" url))
  (try
    (let [props (get-resource-props url)
          resource-name (util/resource-url->name url)
          archive-url (util/resource-url->archive-url url)
          uuid (util/gen-uuid)
          local-csv-path (util/mk-local-path path resource-name uuid date-str)
          _ (clojure.java.io/make-parents local-csv-path)
          records (dwca/open archive-url :path path)
          vals (map (partial prep-record props) records)
          out (io/writer (io/file local-csv-path) :encoding "UTF-8")]
      (do
        (prn (format "%s records found" (nth props 11)))
        (prn (format "Writing to %s" local-csv-path))
        (with-open [f out]
          (csv/write-csv f vals :separator \tab :quote \"))
        (prn (format "Done harvesting %s" resource-name))))
    (catch Exception e (prn "Error harvesting" url (.getMessage e))
           (prn (throw e)))))

(defn url->ipt
  [url]
  (let [sql (format "SELECT ipt FROM %s WHERE url='%s' LIMIT 1" STAGING-TABLE url)]
    (:ipt (first (execute-sql sql)))))

(defn url->icode
  [url]
  (let [sql (format "SELECT icode FROM %s WHERE url='%s' LIMIT 1" STAGING-TABLE url)]
    (:icode (first (execute-sql sql)))))

(defn url->networks
  [url]
  (let [sql (format "SELECT networks FROM %s WHERE url='%s' LIMIT 1" STAGING-TABLE url)]
    (->> (execute-sql sql)
         first
         :networks
         (clojure.string/join ","))))

(defn fetch-url
  "Return HTML from supplied URL."
  [url]
  (html/html-resource (java.net.URL. url)))

(defn get-count
  "Return record count scraped from supplied resource URL."
  [url]
  (try
    (let [html (fetch-url url)
          tds (html/select html [:td])
          dwca-url (util/resource-url->archive-url url)
          node (first
                (filter
                 #(= (:href (:attrs (first (:content %)))) dwca-url)
                 tds))
          token (second (:content node))
          tokens (s/split token #" ")
          count (s/replace (nth tokens 3) "," "")]
      (if (empty? count)
        (throw Exception)
        count))
    (catch Exception e
      (prn (format "Unable to scrape size/count for %s" url))
      "-1")))

(defn get-citation
  [eml]
  (let [citation (.getCitation eml)]
    (if citation (.getCitation citation) nil)))

(defn mk-resource-map
  "Return map of resource table columns from supplied IPT resource URL."
  [url]
  (let [icode (url->icode url)
        ipt (url->ipt url)
        networks (url->networks url)
        eml-url (util/resource-url->eml-url url)
        eml (EmlFactory/build (io/input-stream eml-url))
        row {:title (.getTitle eml)
             :icode icode
             :ipt ipt
             :url url
             :eml eml-url
             :dwca (s/replace url "resource" "archive")
             :pubdate (.toString (.getPubDate eml))
             :orgname (.getOrganisation (.getContact eml))
             :description (.getAbstract eml)
             :emlrights (.getIntellectualRights eml)
             :contact (.getCreatorName eml)
             :email (.getCreatorEmail eml)
             :count (get-count url)
             :citation (get-citation eml)
             :networks networks}]
    row))

(defn get-resource-urls
  "Return vector of resource table row maps from CartoDB."
  [table & {:keys [limit] :or {limit nil}}]
  (let [sql (format "SELECT url FROM %s WHERE ipt=true ORDER BY cartodb_id" table)
        sql (if (nil? limit) sql (format "%s LIMIT %s" sql limit))]
    (map :url (execute-sql sql))))

(defn sync-resource
  "Sync resource at url to CartoDB."
  [url]
  (try
    (prn (format "Syncing: %s" url))
    (let [resource (mk-resource-map url)
          sql (cartodb-utils/maps->insert-sql HARVEST-TABLE resource)]      
      (execute-sql sql))
    (catch Exception e
      (prn (format "SYNC FAIL: %s (%s)" url (.getMessage e))))))

(defn sync-resource-table
  "Sync resource table on CartoDB by populating from EML and resource_staging."
  []
  (let [urls (get-resource-urls STAGING-TABLE)]
    (cartodb/query (format "DELETE FROM %s" HARVEST-TABLE) "vertnet" :api-key util/api-key)
    (doall
     (map sync-resource urls))))

(defn harvest-resource
  [resource-url local-path date-str]
  (try
    (resource->csv resource-url local-path date-str)
    (catch Exception e
      (prn (format "ERROR: Resource %s (%s)" resource-url (.getMessage e)))
      (throw e)
      nil)))

(defn get-harvest-urls
  "Try getting URLs to harvest from a textfile of paths, a collection or finally from
  `HARVEST-TABLE`."
  [& {:keys [path-file path-coll] :or {path nil path-list nil}}]
  (cond
   path-file (util/parse-path-file path-file)
   path-coll path-coll
   :else (get-resource-urls HARVEST-TABLE)))

(defn harvest-all
  "Harvest all resources from resource table on CartoDB to Google Cloud Storage."
  [local-path & {:keys [sync path-file path-coll date] :or {sync false path-file nil path-coll nil date (util/todays-date)}}]
  (if sync
    (sync-resource-table))
  (let [resource-urls (get-harvest-urls :paths-file path-file :path-coll path-coll)
        harvest-fn #(harvest-resource % local-path date)]
    (prn (format "Harvesting %s resources" (count resource-urls)))
    (doall
     (map harvest-fn resource-urls))
    (prn (format "Syncing %s/%s to %s" local-path date GS-PATH))
    (clojure.java.shell/sh "python" (.getPath (io/resource "send_to_gcs.py"))
                           (format "%s/%s" local-path date) GS-PATH)
    (prn "Harvest complete.")))

(def line "Wed Apr 18 00:00:00 UTC 2012	http://fmipt.fieldmuseum.org:8080/ipt/resource.do?r=fm_birds	http://fmipt.fieldmuseum.org:8080/ipt/eml.do?r=fm_birds	http://fmipt.fieldmuseum.org:8080/ipt/archive.do?r=fm_birds	Field Museum of Natural History (Zoology) Bird Collection	FMNH	The Division of Birds houses the third largest scientific bird collection in the United States. The main collection contains over 480,000 specimens, including 600 holotypes, 70,000 skeletons, and 7,000 fluid specimens. In addition, the division houses 21,000 egg sets and 200 nests. The scope of the collection is world-wide; all bird families but one are represented, as are 90% of the world's genera and species. Included among its many historically and scientifically valuable individual collections are the H. B. Conover Game Bird Collection, Good's and Van Someren's African collections, C. B. Cory's West Indian collection, the Bishop Collection of North American birds, a large portion of W. Koelz's material from India and the Middle East, and many separate collections from South America, Africa (Hoogstraal from Egypt) and the Philippines (Rabor).	Sharon Grant	Field Museum of Natural History	sgrant@fieldmuseum.org	\"Copyright © 2012 The Field Museum of Natural History
Full details may be found at http://fieldmuseum.org/about/copyright-information\"	FMNH	505538	1743420						PreservedSpecimen			468422	Birds	Birds	Africa			Malawi		Malawi			8																														Africa, Malawi, Rumphi: Khuta maji, Vwaza Marsh, Vwaza Wildlife Reserve												FMNH										Khuta maji, Vwaza Marsh, Vwaza Wildlife Reserve																				1170	10							skin(r)		MLW-3422												8	Rumphi						1170 -						2009				FMNH				FMNH						birds-12-jul-2012								Animalia Chordata Aves Passeriformes Estrildidae	Animalia	Chordata	Aves	Passeriformes	Estrildidae	Pytilia		melba	melba	Pytilia melba melba							ICZN																;")
