(ns gulo.util-test
  "Unit test the gulo.util namespace."
  (:use gulo.util
        [midje sweet])
  (:require [clj-time.format :as f]))

(fact "Check `todays-date`."
  (type (todays-date)) => java.lang.String)

(fact "Check `mk-stats-out-path`."
  (mk-stats-out-path "/tmp/stats" "2013-04-16" "taxon") => "/tmp/stats/2013-04-16/taxon")

(fact "Check `todays-date` - make sure has the correct format."
  (let [date-str (f/unparse (f/formatters :year-month-day) (f/parse (todays-date)))
        date-tuple (seq (.split date-str "-"))]
    (count date-tuple) => 3
    (->> (first date-tuple)
        (read-string)
        (<= 2013)) => true))

(fact "Check `mk-stats-out-path`"
  (mk-stats-out-path "/tmp/stats" "2013-01-01" "taxon")
  => "/tmp/stats/2013-01-01/taxon")

(fact "Check `line-breaks->spaces`."
  (line-breaks->spaces "a\nb\rc") => "a b c"
  (line-breaks->spaces nil) => "")

(fact "Check resource-url->name"
  (resource-url->name
   "http://ipt.vertnet.org:8080/ipt/resource.do?r=ttrs_birds") => "ttrs_birds")

(fact "Check resource-url->archive-url"
  (resource-url->archive-url
   "http://ipt.vertnet.org:8080/ipt/resource.do?r=ttrs_birds")
  => "http://ipt.vertnet.org:8080/ipt/archive.do?r=ttrs_birds")

(fact "Check mk-local-path"
  (mk-local-path "/tmp/vn" "ttrs_birds" "asdfjkl")
  => "/tmp/vn/ttrs_birds-asdfjkl.csv")

(fact "Check `mk-s3-path`."
  (mk-s3-path "vertnet" "data/staging" "ttrs_birds" "asdfjkl")
  => "vertnet/data/staging/ttrs_birds-asdfjkl.csv")
