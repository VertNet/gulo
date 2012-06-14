(ns gulo.core-test
  (:use gulo.core
        [midje sweet]
        [clojure.string :only (split)])
  (:import [com.google.common.io Files]))

(fact
  "Check harvesting."
  (let [source [["http://vertnet.nhm.ku.edu:8080/ipt/archive.do?r=ttrs_mammals"]]
        temp-dir (Files/createTempDir)
        sink-path (.getPath temp-dir)]
    (harvest source sink-path)
    (println sink-path)
    (count (split (slurp (str sink-path "/part-00000")) #"\n")) => 968))
