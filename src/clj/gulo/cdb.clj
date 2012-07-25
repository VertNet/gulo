(ns gulo.cdb
  "This namespace contains CartoDB functions for setting up database tables."
  (:use [gulo.util]
        [clojure.data.json :only (read-json)]
        [clojure.string :only (join)]
        [clojure.contrib.shell-out :only (sh)])
  (:require [clojure.java.io :as io]
            [cartodb.core :as cartodb]
            [clojure.java.io :as jio])
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
  [table column index & {:keys [unique execute account]
                         :or {unique false execute true account nil}}]
  (let [account (if (not account) (:user creds) account)
        sql (sql-builder "CREATE"
                             (if unique "UNIQUE" "")
                             "INDEX" index
                             "ON" table "(" column ");")]
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
  (create-index "occ" "tax_loc_id" "occ_tax_loc_id_idx"))

(defn- wire-tax-table
  [& {:keys [delete drop-geom] :or {delete false drop-geom false}}]
  (if drop-geom (drop-column "tax" "the_geom" :cascade true))
  (create-index "tax" "tax_id" "tax_tax_id_idx" :unique true)
  (create-index "tax" "name" "tax_name_idx" :unique true))

(defn- wire-loc-table
  [& {:keys [delete drop-geom] :or {delete false drop-geom false}}]
  (create-index "loc" "loc_id" "loc_loc_id_idx" :unique true))

(defn- wire-tax-loc-table
  [& {:keys [delete drop-geom] :or {delete false drop-geom false}}]
  (if drop-geom (drop-column "tax_loc" "the_geom" :cascade true))
  (create-index "tax_loc" "tax_loc_id" "tax_loc_tax_loc_id_idx" :unique true)
  (create-index "tax_loc" "tax_id" "tax_loc_tax_id_idx")
  (create-index "tax_loc" "loc_id" "tax_loc_loc_id_idx"))

(defn prepare-zip
  [table-name table-cols path out-path]
  (let [file-path (str out-path "/" table-name ".csv")
        zip-path (str out-path "/" table-name ".zip")
        bom (.getPath (io/resource "bom.sh"))]
    (jio/copy (File. path) (File. file-path) :encoding "UTF-8")
    ;; TODO: This sh is brittle business
    (sh bom file-path)
    (sh "sed" "-i" (str "1i " (join \tab table-cols)) file-path) ;; Add header to file
    (sh "zip" "-j" "-r" "-D" zip-path file-path)
    zip-path))

(defn prepare-tables
  []
  (let [sink "/mnt/hgfs/Data/vertnet/gulo/tables"
        occ-source "/mnt/hgfs/Data/vertnet/gulo/hfs/occ/part-00000"        
        tax-source "/mnt/hgfs/Data/vertnet/gulo/hfs/tax/part-00000"
        loc-source "/mnt/hgfs/Data/vertnet/gulo/hfs/loc/part-00000"
        tax-loc-source "/mnt/hgfs/Data/vertnet/gulo/hfs/tax-loc/part-00000"]
    (prepare-zip "occ" occ-columns occ-source sink)
    (prepare-zip "tax" ["tax_id" "name"] tax-source sink)
    (prepare-zip "loc" ["loc_id" "lat" "lon" "wkt_geom"] loc-source sink)
    (prepare-zip "tax-loc" ["tax_loc_id" "tax_id" "loc_id"] tax-loc-source sink)))

(defn wire-tables
  []
  (wire-occ-table :drop-geom true)
  (wire-tax-table :drop-geom true)
  (wire-loc-table)
  (wire-tax-loc-table :drop-geom true))

(comment
  ;; After harvest and MapReduce steps:
  (prepare-tables)
  ;; Then for now manually upload ZIPs to CartoDB and finally:
  (wire-tables))
