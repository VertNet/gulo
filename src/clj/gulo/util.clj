(ns gulo.util
  "This namespace contains utility functions."  
  (:use [cartodb.client :as cdb :only (query)]))

(defn dwca-urls
  "Return collection of Darwin Core Archive URLs."
  []
  (vec (map #(vals %) (cdb/query "vertnet" "SELECT dwca_url FROM publishers"))))

(defn gen-uuid
  "Return a randomly generated UUID string."
  [& x] ;; Cascalog ArityException: Wrong number of args without [& x]
  (str (java.util.UUID/randomUUID)))

;; Valid ranges for latitude and longitude.
(def latlon-range {:lat-min -90 :lat-max 90 :lon-min -180 :lon-max 180})

(defn read-latlon
  "Converts lat and lon values from string to number."
  [lat lon]
  {:pre [(instance? java.lang.String lat)
         (instance? java.lang.String lon)]}
  [(read-string lat) (read-string lon)])

(defn latlon-valid?
  "Return true if lat and lon are valid, otherwise return false."
  [lat lon]
  (try
    (let [{:keys [lat-min lat-max lon-min lon-max]} latlon-range
          [lat lon] (read-latlon lat lon)]
      (and (<= lat lat-max)
           (>= lat lat-min)
           (<= lon lon-max)
           (>= lon lon-min)))
    (catch Exception e false)))

(defn occurrence-table-header
  "Return the occurrence table header."
  []
  (join "	"
        (for [x (field-keys rec)]
          (symbol (clojure.string/replace (lower-case (str x)) ":" "")))))
