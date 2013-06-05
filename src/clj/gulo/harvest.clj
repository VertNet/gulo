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
        [dwca.core :as dwca :only (open field-vals fields)]
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
            [gulo.util :as util])
  (:import [java.io File]
           [org.gbif.dwc.record DarwinCoreRecord]
           [org.gbif.metadata.eml EmlFactory]))

(defn prepend-props
  [vals props]
  (concat props vals))

(defn prepend-uuid
  "Prepend UUID to sequence of vals."
  [vals]
  (cons (util/gen-uuid) vals))

(defn file->s3
  "Upload file at supplied path to S3 path."
  [path s3-path]
  (let [src (hfs-textline path)
        sink (hfs-textline s3-path :sinkmode :replace)]
    (?- sink src)))

(defn prep-records
  [records field-vals props]
  (->> records
       (map field-vals)
       (map prepend-uuid)
       (map #(prepend-props % props))
       (map #(concat % [";"]))))

(defn resource->s3
  "Upload Darwn Core records from supplied IPT resource URL to S3."
  [path url props bucket s3-base-path]
  (prn (format "Downloading: %s records from %s" (nth props 11) url))
  (try
    (let [resource-name (util/resource-url->name url)
          uuid (util/gen-uuid)
          local-csv-path (util/mk-local-path path resource-name uuid)
          s3-full-path (util/mk-full-s3-path bucket s3-base-path resource-name uuid)
          stub (last (.split s3-full-path "@"))
          archive-url (util/resource-url->archive-url url)
          records (dwca/open archive-url :path path)
          vals (prep-records records field-vals props)
          out (io/writer (io/file local-csv-path) :encoding "UTF-8")]
      (do
        (prn (format "Writing to %s" local-csv-path))
        (with-open [f out]
          (csv/write-csv f vals :separator \tab :quote \"))
        (do
          (prn (format "Uploading %s to S3: %s" local-csv-path stub))
          (file->s3 local-csv-path s3-full-path))
        (prn "Done harvesting" resource-name)
        (cio/delete-file-recursively local-csv-path)))
    (catch Exception e (prn "Error harvesting" url (.getMessage e))
           (prn (throw e)))))

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
      count)
    (catch Exception e
      (prn (format "Unable to scrape size/count for %s" url))
      -1)))

(defn get-citation
  [eml]
  (let [citation (.getCitation eml)]
    (if citation (.getCitation citation) nil)))

(defn resource-row
  "Return map of resource table columns from supplied IPT resource URL."
  [url icode ipt]
  (let [eml-url (util/resource-url->eml-url url)
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
             :citation (get-citation eml)}]
    row))

(defn query-resource-rows
  []
  (let [sql "SELECT url, icode, ipt FROM resource_staging WHERE ipt=true ORDER BY cartodb_id"
        rows (:rows (cartodb/query sql "vertnet" :api-key util/api-key))]
    rows))

(defn resource-staging-rows
  "Return vector of resource-rows from resource_staging table on CartoDB."
  [& [rows]]
  (let [rows (or rows (query-resource-rows))
        resource-rows (map #(resource-row (:url %) (:icode %) (:ipt %)) rows)]
    resource-rows))

(defn sync-resource
  "Sync resource map to CartoDB."
  [resource]
  (try
    (prn (format "Syncing: %s" (:url resource)))
    (let [sql (cartodb-utils/maps->insert-sql "resource" resource)]      
      (cartodb/query sql "vertnet" :api-key util/api-key))
    (catch Exception e
      (prn (format "SYNC FAIL: %s (%s)" (:url resource) (.getMessage e))))))

(defn sync-resource-table
  "Sync resource table on CartoDB by populating from EML and resource_staging."
  []
  (let [rows (resource-staging-rows)]
    (cartodb/query "DELETE FROM resource" "vertnet" :api-key util/api-key)
    (doall
     (map sync-resource rows))))

(defn get-resources
  "Return vector of resource table row maps from CartoDB."
  [& {:keys [limit] :or {limit nil}}]
  (let [sql "SELECT * FROM resource WHERE ipt=true ORDER BY cartodb_id"
        sql (if (nil? limit) sql (format "%s LIMIT %s" sql limit))
        resources (:rows (cartodb/query sql "vertnet" :api-key util/api-key))]
    resources))

(defn get-resource-props
  "Extract and clean up props in resource map."
  [resource-map]
  (let [props (map #(% resource-map) f/resource-fields)]
    (map util/line-breaks->spaces (flatten props))))

(defn harvest-resource
  [resource local-path bucket s3-path]
  (try
    (let [url (:url resource)
          props (get-resource-props resource)]
      (resource->s3 local-path url props bucket s3-path))
    (catch Exception e
      (prn (format "ERROR: Resource %s (%s)" (:url resource) (.getMessage e)))
      (throw e)
      nil)))

(defn harvest-all
  "Harvest all resources from resource table on CartoDB to S3."
  [local-path s3-bucket s3-path & {:keys [sync] :or {sync false}}]
  (if sync
    (sync-resource-table))
  (let [resources (get-resources)]
    (doall
     (prn (format "Harvesting %s resources to %s" (count resources) s3-path))
     (map #(harvest-resource % local-path s3-bucket s3-path) resources))))

(def line "Wed Apr 18 00:00:00 UTC 2012	http://fmipt.fieldmuseum.org:8080/ipt/resource.do?r=fm_birds	http://fmipt.fieldmuseum.org:8080/ipt/eml.do?r=fm_birds	http://fmipt.fieldmuseum.org:8080/ipt/archive.do?r=fm_birds	Field Museum of Natural History (Zoology) Bird Collection	FMNH	The Division of Birds houses the third largest scientific bird collection in the United States. The main collection contains over 480,000 specimens, including 600 holotypes, 70,000 skeletons, and 7,000 fluid specimens. In addition, the division houses 21,000 egg sets and 200 nests. The scope of the collection is world-wide; all bird families but one are represented, as are 90% of the world's genera and species. Included among its many historically and scientifically valuable individual collections are the H. B. Conover Game Bird Collection, Good's and Van Someren's African collections, C. B. Cory's West Indian collection, the Bishop Collection of North American birds, a large portion of W. Koelz's material from India and the Middle East, and many separate collections from South America, Africa (Hoogstraal from Egypt) and the Philippines (Rabor).	Sharon Grant	Field Museum of Natural History	sgrant@fieldmuseum.org	\"Copyright © 2012 The Field Museum of Natural History
Full details may be found at http://fieldmuseum.org/about/copyright-information\"	FMNH	505538	1743420						PreservedSpecimen			468422	Birds	Birds	Africa			Malawi		Malawi			8																														Africa, Malawi, Rumphi: Khuta maji, Vwaza Marsh, Vwaza Wildlife Reserve												FMNH										Khuta maji, Vwaza Marsh, Vwaza Wildlife Reserve																				1170	10							skin(r)		MLW-3422												8	Rumphi						1170 -						2009				FMNH				FMNH						birds-12-jul-2012								Animalia Chordata Aves Passeriformes Estrildidae	Animalia	Chordata	Aves	Passeriformes	Estrildidae	Pytilia		melba	melba	Pytilia melba melba							ICZN																;")
