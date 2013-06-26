(ns gulo.util
  "This namespace contains utility functions."
  (:use [dwca.core :as dwca :only (index-of field-vals)]
        [clojure.data.json :only (read-json)])
  (:import [org.gbif.dwc.record DarwinCoreRecord])
  (:require [clj-time.core :as time]
            [clj-time.format :as f]
            [clojure.string :as s]
            [clojure.java.io :as io]))

;; eBird data resource id:
(def ^:const EBIRD-ID "43")

;; Slurps resources/creds.json for CartoDB OAuth: {"key" "secret" "user" "password"}
(def cartodb-creds (read-json (slurp (io/resource "creds.json"))))

;; CartoDB API key:
(def api-key (:api_key cartodb-creds))

;; Slurps resources/aws.json for Amazon S3: {"access-key" "secret-key"}
(def aws-creds (read-json (slurp (io/resource "aws.json"))))

(defn todays-date
  "Returns current date as \"YYYY-MM-dd\".

   Usage:
     (todays-date)
     ;=> \"2013-04-16\""
  []
  (f/unparse (f/formatters :year-month-day) (time/now)))

(defn add-fields
  "Adds an arbitrary number of fields to tuples in a cascalog query."
  [& fields]
  (vec fields))

(defn mk-stats-out-path
  "Build output path, using a base path, query name, and the current date.

   Usage:
     (mk-stats-out-path \"/tmp/stats\" \"taxon\")
     ;=> \"/tmp/stats/2013-04-17/taxon\""
  [base-path date-str query-name]
  (format "%s/%s/%s" base-path date-str query-name))

(defn line-breaks->spaces
  [s]
  (if (nil? s)
    ""
    (-> s
        (s/replace "\n" " ")
        (s/replace "\r" " "))))

(defn resource-url->name
  [url]
  (second (s/split url #"=")))

(defn mk-local-path
  [path resource-name uuid]
  (format "%s/%s-%s.csv" path resource-name uuid))

(defn mk-s3-path
  [bucket s3-path resource-name uuid]
  (format "%s/%s/%s-%s" bucket s3-path resource-name uuid))

(defn mk-full-s3-path
  [bucket s3-path resource-name uuid & [creds-map]]
  (let [aws-creds (or creds-map aws-creds)
        key (:access-id aws-creds)
        secret (:secret-key aws-creds)
        path (mk-s3-path bucket s3-path resource-name uuid)]
    (format "s3n://%s:%s@%s" key secret path)))

(defn resource-url->archive-url
  [url]
  (s/replace url "resource" "archive"))

(defn resource-url->eml-url
  [url]
  (s/replace url "resource" "eml"))

;; these functions were copied directly from teratorn.common on
;; 5/29/13

(defn not-ebird
  "Return true if supplied id represents an eBird record, otherwise false."
  [id]
  (not= id EBIRD-ID))

(defn parse-double
  "Wrapper for `java.lang.Double/parseDouble`, suitable for use with `map`.

   Usage:
     (parse-double \"-.1\")
     ;=> -0.1
     (map parse-double [\"100\" \"-.1\"])
     ;=> (100 -0.1)"
  [s]
  (java.lang.Double/parseDouble s))

(defn str->num-or-empty-str
  "Convert a string to a number with read-string and return it. If not a
   number, return an empty string.

   Try/catch form will catch exception from using read-string with
   non-decimal degree or entirely wrong lats and lons (a la 5°52.5'N, 6d
   10m s S, or 0/0/0 - all have been seen in the data).

   Note that this will also handle major errors in the lat/lon fields
   that may be due to mal-formed or non-standard input text lines that
   would otherwise cause parsing errors."
  [s]
  (try
    (let [parsed-str (parse-double s)]
      (if (number? parsed-str)
        parsed-str
        ""))
    (catch Exception e "")))

(defn handle-zeros
  "Handle trailing decimal points and trailing zeros in a numeric
   string. A trailing decimal point is removed entirely, while
   trailing zeros are only dropped if they immediately follow the
   decimal point.

   Usage:
     (handle-zeros \"3.\")
     ;=> \"3\"

     (handle-zeros \"3.0\")
     ;=> \"3\"

     (handle-zeros \"3.00\")
     ;=> \"3\"

     (handle-zeros \"3.001\")
     ;=> \"3.001\"

     (handle-zeros \"3.00100\")
     ;=>\"3.00100\""
  [s]
  (let [[head tail] (s/split s #"\.")]
    (if (or (zero? (count tail)) ;; nothing after decimal place
            (zero? (Integer/parseInt tail))) ;; all zeros after decimal place
      (str (Integer/parseInt head))
      s)))

(defn round-to
  "Round a value to a given number of decimal places and return a
   string. Note that this will drop all trailing zeros, and values like
   3.0 will be returned as \"3\""
  [digits n & {:keys [format-str] :or [format-str false]}]
  (let [formatter (str "%." (str digits) "f")
        string-or-double (fn [fmt s] (if (true? format-str)
                                      s
                                      (Double/parseDouble s)))]
    (if (= "" n)
      n
      (->> (format formatter (double n))
           reverse
           (drop-while #{\0})
           reverse
           (apply str)
           (handle-zeros)
           (string-or-double format-str)))))

(defn makeline
  "Returns a string line by joining a sequence of values on tab."
  [& vals]
  (s/join \tab vals))

 (defn split-line
  "Returns vector of line values by splitting on tab."
  [line]
  (let [line (if (.endsWith line "\t") (str line " ") line)]
    (map #(.trim %) (vec (.split line "\t")))))

(defn gen-uuid
  "Return a randomly generated UUID string."
  [& x]
  (str (java.util.UUID/randomUUID)))

(defn valid-latlon?
  "Return true if lat and lon are valid decimal degrees,
   otherwise return false. Assumes that lat and lon are both either numeric
   or string."
  [lat lon]
  (if (or (= "" lat)
          (= "" lon))
    false   
    (try
      (let [[lat lon] (if (number? lat)
                        [lat lon]
                        (map parse-double [lat lon]))
            latlon-range {:lat-min -90 :lat-max 90 :lon-min -180 :lon-max 180}
            {:keys [lat-min lat-max lon-min lon-max]} latlon-range]
        (and (<= lat lat-max)
             (>= lat lat-min)
             (<= lon lon-max)
             (>= lon lon-min)))
      (catch Exception e false))))

(defn valid-name?
  "Return true if name is valid, otherwise return false."
  [name]
  (and (not= name nil) (not= name "") (not (.contains name "\""))))

(defn quoter
  "Return x surrounded in double quotes with any double quotes escaped with
  double quotes, if needed."
  [x]
  (if (or (and (.startsWith x "\"") (.endsWith x "\""))
          (= "" x)
          (not (.contains x "\""))) 
    (s/replace x "'" "''")
    (let [val (format "\"%s\"" (.replace x "\"" "\"\"") "\"" "\"\"")
          val (s/replace val "'" "''")]
      val)))
  
(defn quotemaster
  "Maps quoter function over supplied vals."
  [& vals]
  (vec (map quoter vals)))

(def season-map
  "Encodes seasons as indices: 0-3 for northern hemisphere, 4-7 for the south"
  {"Northern winter" 0
   "Northern spring" 1
   "Northern summer" 2
   "Northern fall" 3
   "Southern winter" 4
   "Southern spring" 5
   "Southern summer" 6
   "Southern fall" 7})

(defn parse-hemisphere
  "Returns a quarter->season map based on the hemisphere."
  [h]
  (let [n_seasons {0 "winter" 1 "spring" 2 "summer" 3 "fall"}
        s_seasons {0 "summer" 1 "fall" 2 "winter" 3 "spring"}]
    (if (= h "Northern") n_seasons s_seasons)))

(defn get-season-idx
  "Returns season index (roughly quarter) given a month."
  [month]
  {:pre [(>= 12 month)]}
  (let [season-idxs {11 0 12 0 1 0
                     2 1 3 1 4 1
                     5 2 6 2 7 2
                     8 3 9 3 10 3}]
    (get season-idxs month)))

(defn get-season-str
  "Based on the latitude and the month, return a season index
   as given in season-map.

   Usage:
     (get-season 40.0 -1.0 1)
     ;=> \"Northern winter\""
  [lat lon month]
  (if (or (not (valid-latlon? lat lon))
          (= "" month))
    "unknown"
    (let [lat (if (string? lat) (read-string lat) lat)
          month (if (string? month) (read-string month) month)
          hemisphere (if (pos? lat) "Northern" "Southern")
          season (get (parse-hemisphere hemisphere)
                      (get-season-idx month))]
      (format "%s %s" hemisphere season))))

(defn cleanup-data
  "Cleanup data by handling rounding, missing data, etc."
  [digits lat lon prec year month & {:keys [format-str] :or [format-str false]}]
  (let [[lat lon clean-prec clean-year clean-month]
        (map str->num-or-empty-str [lat lon prec year month])]
    (concat (map #(round-to digits % :format-str format-str) [lat lon clean-prec])
            (map str [clean-year clean-month]))))

(defn positions
  "Returns a lazy sequence containing the positions at which pred
   is true for items in coll."
  [pred coll]
  (for [[idx elt] (map-indexed vector coll) :when (pred elt)] idx))

(defn kw->field-str
  "Convert a field name keyword or string to a Cascalog-style field string.

   Usage:
     (field->field-str :pubdate)
     ;=> \"?pubdate\"

     (field->field-str \"pubdate\")
     ;=> \"?pubdate\""
  [kw]
  (str "?" (name kw)))

(defn str->cascalog-field
  "Prepend `?` to a string to make it suitable for use as a Cascalog field."
  [s]
  (str "?" s))

(defn handle-sql-reserved
  "Prepend underscore to strings in vector that match reserved words."
  [v]
  (let [reserved (set ["group" "order"])
        idxs (positions #(contains? reserved %) v)
        sqlize (fn [s] (str "_" s))]
    (reduce #(update-in % [%2] sqlize) v idxs)))

(defn field->nullable
  "Convert a non-nullable Cascalog field name string into a nullable
  one (i.e. replace ? with !)."
  [s]
  (s/replace s "?" "!"))

(defn nils->spaces
  "Replace nils in fields with spaces. See test for appropriate usage
  within Cascalog."
  [& fields]
  (let [fields (flatten fields)
        replacer #(if (nil? %) "" %)]
    (map replacer fields)))

(defn positions
  "Returns a lazy sequence containing the positions at which pred
   is true for items in coll."
  [pred coll]
  (for [[idx elt] (map-indexed vector coll) :when (pred elt)] idx))
