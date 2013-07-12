(ns gulo.util-test
  "Unit test the gulo.util namespace."
  (:use gulo.util
        cascalog.api
        [midje cascalog sweet])
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

(fact "Check `add-fields`."
  (let [src [[1 2]]]
    (<- [?a ?b ?c]
        (src ?a ?b)
        (add-fields 3 :> ?c))) => (produces [[1 2 3]]))

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
  => "vertnet/data/staging/ttrs_birds-asdfjkl")

(fact "Check `mk-full-s3-path`."
  (mk-full-s3-path "vertnet" "data/staging" "ttrs_birds" "asdfjkl"
                   {:access-id "asdf" :secret-key "jkl"})
  => "s3n://asdf:jkl@vertnet/data/staging/ttrs_birds-asdfjkl")

;; tests copied directly from teratorn.common-test on 5/29/13

(fact "Check `kw->field-str`."
  (kw->field-str :pubdate) => "?pubdate"
  (kw->field-str "pubdate") => "?pubdate")

(tabular
 (fact "Check valid-latlon? function."   
   (valid-latlon? ?lat ?lon) => ?result)
 ?lat ?lon ?result
 "41.850033" "-87.65005229999997" true
 "90" "180" true
 "-90" "-180" true
 "90" "-180" true
 "-90" "180" true
 "0" "0" true
 "90.0" "180.0" true
 "-90.0" "-180.0" true
 "90.0" "-180.0" true
 "-90.0" "180.0" true
 "0.0" "0.0" true 
 41.850033 -87.65005229999997 true
 "-91" "0" false
 "91" "0" false
 "-181" "0" false
 "181" "0" false
 "asdf" "asdf" false
 1.2 2.3 true
 "1.2" "2.3" true
 100 100. false
 "100" "100." false
 "100" 1 false
 "100" "-.1" false)

(facts
  "Test str->num-or-empty-str"
  (str->num-or-empty-str "1.2") => 1.2
  (str->num-or-empty-str "\\N") => "")

(facts
  "Test handle-zeros"
  (handle-zeros "3.") => "3"
  (handle-zeros "3.0") => "3"
  (handle-zeros "3.0000") => "3"
  (handle-zeros "3.00100") => "3.00100"
  (handle-zeros "3") => "3"
  (handle-zeros "3.445480") => "3.445480"
  (handle-zeros "3.1234567890") => "3.1234567890")

(future-fact "Test gen-uuid")
(future-fact "Test quoter")
(future-fact "Test quote-master")

(facts
  "Test round-to"
  (round-to 7 3 :format-str true) => "3"
  (round-to 7 3.1234567890 :format-str true) => "3.1234568"
  (round-to 7 3.0 :format-str true) => "3"
  (round-to 7 3.120 :format-str false) => 3.12
  (round-to 7 3.120) => 3.12
  (round-to 7 3.1000000) => 3.1
  (round-to 7 3.10000009) => 3.1000001
  (round-to 7 300) => 300.0
  (round-to 7 300.0) => 300.0
  (round-to 7 300.0 :format-str true) => "300"
  (round-to 7 300.123456789) => 300.1234568
  (round-to 7 -3) => -3.0
  (round-to 7 -3 :format-str true) => "-3"
  (round-to 7 -3.1234567890) => -3.1234568
  (round-to 7 -3.0) => -3.0
  (round-to 7 -3.0 :format-str true) => "-3"
  (round-to 7 -3.120) => -3.12
  (round-to 7 -3.1000000) => -3.1
  (round-to 7 -3.10000009) => -3.1000001
  (round-to 7 -300) => -300.0
  (round-to 7 -300 :format-str true) => "-300"
  (round-to 7 -300.0) => -300.0
  (round-to 7 -300.0 :format-str true) => "-300"
  (round-to 7 -300.123456789) => -300.1234568)

(fact
  "Test parse-hemisphere"
  (parse-hemisphere "Northern") => {0 "winter" 1 "spring" 2 "summer" 3 "fall"}
  (parse-hemisphere "Southern") => {0 "summer" 1 "fall" 2 "winter" 3 "spring"})

(fact
  "Test get-season-idx"
  (get-season-idx 1) => 0
  (get-season-idx 3) => 1
  (get-season-idx 4) => 1
  (get-season-idx 6) => 2
  (get-season-idx 7) => 2
  (get-season-idx 13) => nil
  (get-season-idx 0) => nil
  (get-season-idx "January") => nil)

(fact
  "Test get-season"
  (get-season-str 1 1 1) => "Northern winter"
  (get-season-str -1 1 1) => "Southern summer"
  (get-season-str 1 1 3) => "Northern spring"
  (get-season-str -1 1 3) => "Southern fall"
  (get-season-str 1 1 4) => "Northern spring"
  (get-season-str -1 1 4) => "Southern fall"
  (get-season-str 1 1 7) => "Northern summer"
  (get-season-str -1 1 7) => "Southern winter"
  (get-season-str 1 1 10) => "Northern fall"
  (get-season-str -1 1 10) => "Southern spring")

(fact "Test `field->nullable`."
  (field->nullable "?yo") => "!yo")

(fact "Test `nils->spaces`."
  ;; regular
  (nils->spaces [1 2 nil]) => [1 2 ""]

  ;; works within Cascalog
  (let [src [[1 2 nil]]
        fields ["?a" "?b" "?c"]
        fields-nullable (map field->nullable fields)]
    (<- [?a ?b ?c]
        (src :>> fields-nullable)
        (nils->spaces :<< fields-nullable :>> fields)) => (produces [[1 2 ""]])))

(fact "Test `positions`."
  (positions (partial > 1) [0 1 2 3 -1]) => [0 4])

(facts
  "Test `str->num-or-empty-str`."
  (str->num-or-empty-str "1.2") => 1.2
  (str->num-or-empty-str "\\N") => ""
  (str->num-or-empty-str "5°52.5'N") => ""
  (str->num-or-empty-str ".435") => 0.435)
