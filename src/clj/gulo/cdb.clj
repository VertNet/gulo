(ns gulo.cdb
  "This namespace contains CartoDB functions for setting up database tables."
  (:use [gulo.util]
        [clojure.data.json :only (read-json)]
        [clojure.string :only (join)])
  (:require [clojure.java.io :as io]
            [cartodb.client :as cdb]))

(def creds (read-json (slurp (io/resource "creds.json"))))

(defn- drop-column
  [table column & {:keys [cascade execute account]
                   :or {cascade false execute true account nil}}]
  (let [account (if (not account) (:user creds) account)
        sql (cdb/sql-builder "ALTER TABLE" table "DROP COLUMN" column)
        sql (if cascade (str sql " CASCADE;") (str sql ";"))]
    (if execute (cdb/query sql account :oauth creds) sql)))

(defn- drop-table
  [table & {:keys [execute account]
                   :or {execute true account nil}}]
  (let [tables (if (coll? table) (join "," table) table)
        account (if (not account) (:user creds) account)
        sql (cdb/sql-builder "DROP TABLE IF EXISTS" tables ";")]
    (if execute (cdb/query sql account :oauth creds) sql)))

(defn- create-index
  [table column index & {:keys [unique execute account]
                         :or {unique false execute true account nil}}]
  (let [account (if (not account) (:user creds) account)
        sql (cdb/sql-builder "CREATE"
                             (if unique "UNIQUE" "")
                             "INDEX" index
                             "ON" table "(" column ");")]
    (if execute (cdb/query sql account :oauth creds) sql)))

(defn- create-occ-table
  "Create occ table."
  [& {:keys [execute] :or {execute true}}]
  (let [sql "CREATE TABLE IF NOT EXISTS occ ("
        sql (str sql "cartodb_id SERIAL,")
        sql (str sql (join "," (map #(str % " text") occ-columns)))
        sql (str sql ");")
        account (:user creds)]
    (if execute (cdb/query sql account :oauth creds) sql)))

(defn wire-occ-table
  [& {:keys [delete] :or {delete false}}]
  (if delete (drop-table "occ"))
  (create-occ-table)
  ;;(drop-column "occ" "the_geom" :cascade true)
  ;;(drop-column "occ" "the_geom_webmercator" :cascade true)
  (create-index "occ" "occ_id" "occ_occ_id_idx" :unique true)
  (create-index "occ" "tax_loc_id" "occ_tax_loc_id_idx"))

