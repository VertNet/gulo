(ns gulo.cdb
  "This namespace contains CartoDB functions for setting up database tables."
  (:use [gulo.util]
        [clojure.data.json :only (read-json)]
        [clojure.string :only (join)]
        [cascalog.api]
        [clojure.contrib.shell-out :only (sh)])
  (:require [clojure.java.io :as io]
            [cartodb.core :as cartodb]
            [aws.sdk.s3 :as s3])
  (:import [com.google.common.io Files]
           [com.google.common.base Charsets]
           [java.io File FileInputStream FileWriter]
           [net.lingala.zip4j.core ZipFile]
           [net.lingala.zip4j.model ZipParameters]
           [net.lingala.zip4j.util Zip4jConstants]
           [net.lingala.zip4j.exception ZipException]))

(defn sql-builder
  "Return space-separated query."
  [& strs]
  (apply str (interpose " " strs)))

;; Slurps resources/creds.json for OAuth: {"key" "secret" "user" "password"}
(def creds (read-json (slurp (io/resource "creds.json"))))

;; Slurps resources/s3.json for Amazon S3: {"access-key" "secret-key"}
(def s3-creds (read-json (slurp (io/resource "s3.json"))))

(defn- drop-column
  "Drop the supplied column from the supplied table or just return the SQL."
  [table column & {:keys [cascade execute account]
                   :or {cascade false execute true account nil}}]
  (let [account (if (not account) (:user creds) account)
        sql (sql-builder "ALTER TABLE" table "DROP COLUMN" column)
        sql (if cascade (str sql " CASCADE;") (str sql ";"))]
    (if execute (cartodb/query sql account :oauth creds) sql)))

(defn- drop-table
  "Drop the supplied table or just return the SQL."
  [table & {:keys [execute account]
                   :or {execute true account nil}}]
  (let [tables (if (coll? table) (join "," table) table)
        account (if (not account) (:user creds) account)
        sql (sql-builder "DROP TABLE IF EXISTS" tables ";")]
    (if execute (cartodb/query sql account :oauth creds) sql)))

(defn- create-index
  "Create index on table column or just return the SQL."
  [table column index & {:keys [unique execute account lower]
                         :or {unique false execute true account nil lower false}}]
  (let [account (if (not account) (:user creds) account)
        sql (sql-builder "CREATE"
                         (if unique "UNIQUE" "")
                         "INDEX" index
                         "ON" table
                         "("
                         (if lower (str "lower(" column ")") column)
                         ");")]
    (if execute (cartodb/query sql account :oauth creds) sql)))

(defn- create-occ-table
  "Create occ table."
  [& {:keys [execute] :or {execute true}}]
  (let [sql "CREATE TABLE IF NOT EXISTS occ ("
        sql (str sql "cartodb_id SERIAL,")
        sql (str sql (join "," (map #(str % " text") occ-columns)))
        sql (str sql ");")
        account (:user creds)]
    (if execute (cartodb/query sql account :oauth creds) sql)))

(defn- wire-occ-table
  [& {:keys [delete drop-geom] :or {delete false drop-geom false}}]
  (if drop-geom (drop-column "occ" "the_geom" :cascade true))
  (create-index "occ" "occ_id" "occ_occ_id_idx" :unique true)
  (create-index "occ" "tax_loc_id" "occ_tax_loc_id_idx")
  (create-index "occ" "_classs" "occ_class_idx" :lower true)
  (create-index "occ" "icode" "occ_icode_idx" :lower true))

(defn- wire-tax-table
  [& {:keys [delete drop-geom] :or {delete false drop-geom false}}]
  (if drop-geom (drop-column "tax" "the_geom" :cascade true))
  (create-index "tax" "tax_id" "tax_tax_id_idx" :unique true)
  (create-index "tax" "name" "tax_name_idx" :unique true :lower true))

(defn- wire-loc-table
  [& {:keys [delete drop-geom] :or {delete false drop-geom false}}]
  (create-index "loc" "loc_id" "loc_loc_id_idx" :unique true))

(defn- wire-tax-loc-table
  [& {:keys [delete drop-geom] :or {delete false drop-geom false}}]
  (if drop-geom (drop-column "taxloc" "the_geom" :cascade true))
  (create-index "taxloc" "tax_loc_id" "taxloc_tax_loc_id_idx" :unique true)
  (create-index "taxloc" "tax_id" "taxloc_tax_id_idx")
  (create-index "taxloc" "loc_id" "taxloc_loc_id_idx"))

(defn merge-parts
  "Merge all part files into single file for supplied table."
  [table]
  (let [path (str "/mnt/hgfs/Data/vertnet/gulo/hfs/" table)
        output (str path "/" table ".csv")
        x (io/delete-file output true)
        files (vec (filter #(.isFile %) (file-seq (io/file path))))
        out (io/writer (io/file output) :append true :encoding "UTF-8")]
    (map #(io/copy % out :encoding "UTF-8") files)))

(defn s3parts->file
  "Download part files from S3 for supplied table and merge into single file."
  [table & {:keys [local] :or {local "/mnt/hgfs/Data/vertnet/gulo/hfs/"}}]
  (let [sink (str local table)
        key (:access-key s3-creds)
        secret (:secret-key s3-creds)
        source (str "s3n://" key  ":" secret "@gulohfs/" table)
        temp-file (?- (hfs-textline sink :sinkmode :replace)
                      (hfs-textline source))]
    (merge-parts table)))

(defn prepare-zip
  [table-name table-cols path out-path]
  (let [source-path (str path table-name ".csv")
        file-path (str out-path table-name ".csv")
        zip-path (str out-path table-name ".zip")
        bom (.getPath (io/resource "bom.sh"))]
    (io/delete-file file-path true)
    (io/delete-file zip-path true)
    (io/copy (io/file source-path) (io/file file-path) :encoding "UTF-8")
    ;; TODO: This sh is brittle business
    (sh "sed" "-i" (str "1i " (join \tab table-cols)) file-path) ;; Add header to file
    (sh bom file-path)
    (sh "zip" "-j" "-r" "-D" zip-path file-path)))

(defn prepare-tables
  "Prepare tables for CartoDB upload by adding headers, adding BOM, and zipping."
  []
  (let [sink "/mnt/hgfs/Data/vertnet/gulo/tables/"
        occ-source "/mnt/hgfs/Data/vertnet/gulo/hfs/occ/"
        tax-source "/mnt/hgfs/Data/vertnet/gulo/hfs/tax/"
        loc-source "/mnt/hgfs/Data/vertnet/gulo/hfs/loc/"
        tax-loc-source "/mnt/hgfs/Data/vertnet/gulo/hfs/taxloc/"]
    (prepare-zip "occ" occ-columns occ-source sink)
    (prepare-zip "tax" ["tax_id" "name"] tax-source sink)
    (prepare-zip "loc" ["loc_id" "lat" "lon" "wkt_geom"] loc-source sink)
    (prepare-zip "taxloc" ["tax_loc_id" "tax_id" "loc_id"] tax-loc-source sink)))

(defn wire-tables
  "Wire occ, tax, loc, and tax-loc tables by creating indexes and dropping
  unneeded columns."
  []
  (wire-occ-table :drop-geom true)
  (wire-tax-table :drop-geom true)
  (wire-loc-table)
  (wire-tax-loc-table :drop-geom true))
