(ns dwca.core
  "This namespace provides a Clojure API to the GBIF dwca-reader library."
  (:require [clojure.java.io :as io]
            [clj-http.client :as client :only (get)])
  (:import [com.google.common.io Files]
           [java.io File]
           [java.lang.reflect Field]
           [java.net URL]
           [net.lingala.zip4j.core ZipFile]
           [net.lingala.zip4j.exception ZipException]
           [org.gbif.dwc.record DarwinCoreRecord]
           [org.gbif.dwc.text ArchiveFactory]
           [org.gbif.file DownloadUtil]))

(defn gen-uuid
  "Return a randomly generated UUID string."
  [& x]
  (str (java.util.UUID/randomUUID)))

(defprotocol IDarwinCoreRecord
  "Protocol for accessing DarwinCoreRecord fields."
  (fields [x])
  (field-keys [x])
  (field-vals [x]))

(defn index-of
  "Return the index of the supplied field key."
  [rec field-key]
  (.indexOf (field-keys rec) field-key))

(defn field-val
  "Return the string value of the supplied record field."
  [^Field field ^DarwinCoreRecord rec]
  {:pre [(instance? Field field)
         (instance? DarwinCoreRecord rec)]}
  (.setAccessible field true)
  (let [val (.get field rec)]
    (if val (.trim val))))

(extend-protocol IDarwinCoreRecord
  DarwinCoreRecord
  (fields
    [^DarwinCoreRecord x]
    {:pre [(instance? DarwinCoreRecord x)]}
    (zipmap (field-keys x) (field-vals x)))
  (field-keys
    [^DarwinCoreRecord x]
    {:pre [(instance? DarwinCoreRecord x)]}
    (let [fields (->> x .getClass .getDeclaredFields vec)
          super-fields (->> x .getClass .getSuperclass .getDeclaredFields vec)]
      ;; subvec 3 skips the first three declared fields in DarwinCoreTaxon.
      (vec (map #(keyword (clojure.string/lower-case (.getName %)))
                (concat fields (subvec super-fields 3))))))
  (field-vals
    [^DarwinCoreRecord x]
    {:pre [(instance? DarwinCoreRecord x)]}
    (let [fields (->> x .getClass .getDeclaredFields vec)
          super-fields (->> x .getClass .getSuperclass .getDeclaredFields vec)]
      ;; subvec 3 skips the first three declared fields in DarwinCoreTaxon.
      (vec (map #(field-val % x) (concat fields (subvec super-fields 3)))))))

(defn download
  "Downloads a Darwin Core Archive from the supplied URL to the supplied file."
  [url file]
  (let [response (client/get url {:as :byte-array})
        writer (clojure.java.io/output-stream file)]
    (with-open [f writer]
      (.write f (:body response)))))

(defn unzip
  "Unzips the supplied ZIP file into the supplied directory."
  [file dir]
  (let [zipfile (ZipFile. file)]
    (.extractAll zipfile dir)))

(defn get-records
  "Returns a sequence of DarwinCoreRecord objects from an archive directory."
  [dir]
  (let [archive (ArchiveFactory/openArchive (File. dir))]
    (iterator-seq (.iteratorDwc archive))))

(defn archive-name
  "Return archive name from supplied URL as defined by the IPT."
  [url]
  (if (.endsWith url ".zip")
    (str "dwca-" (clojure.string/replace (last (.split url "/")) ".zip" ""))
    (str "dwca-" (nth (.split url "=") 1))))

(defn open
  "Open archive at supplied URL and return a sequence of DarwinCoreRecord objects."
  [url & {:keys [path] :or {path (.getPath (Files/createTempDir))}}]
  (let [archive-name (archive-name url)
        uuid (gen-uuid)
        zip-path (format "%s/%s-%s.zip" path archive-name uuid)
        archive-path (format "%s/%s-%s" path archive-name uuid)]
    (download url zip-path)
    (unzip zip-path archive-path)
    (io/delete-file zip-path)
    (get-records archive-path)))