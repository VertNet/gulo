(ns gulo.util-test
  "Unit test the gulo.util namespace."
  (:use gulo.util
        [midje sweet]))

(tabular
 (fact "Check the read-latlon function."
   (read-latlon ?lat ?lon) => ?result)
 ?lat ?lon ?result
 "41.850033" "-87.65005229999997" [41.850033 -87.65005229999997]
 41.850033 -87.65005229999997 (throws AssertionError)
 "41.850033" -87.65005229999997 (throws AssertionError)
 41.850033 "-87.65005229999997" (throws AssertionError))

(tabular
 (fact "Check latlon-valid? function."   
   (latlon-valid? ?lat ?lon) => ?result)
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
 "0" "0" true
 "0.0" "0.0" true 
 41.850033 -87.65005229999997 (throws AssertionError)
 "-91" "0" false
 "91" "0" false
 "-181" "0" false
 "181" "0" false)
