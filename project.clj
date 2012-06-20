(defproject gulo "0.1.0-SNAPSHOT"
  :description "Shredding Darwin Core Archives with ferocity, strength, and Cascalog."
  :repositories {"conjars" "http://conjars.org/repo/"
                 "gbif" "http://repository.gbif.org/content/groups/gbif/"
                 "maven2" "http://repo2.maven.org/maven2"}
  :source-path "src/clj"
  :resources-path "resources"
  :dev-resources-path "dev"
  :jvm-opts ["-XX:MaxPermSize=256M"
             "-XX:+UseConcMarkSweepGC"
             "-Xms1024M" "-Xmx1048M" "-server"]
  :plugins [[swank-clojure "1.4.0-SNAPSHOT"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [cascalog "1.8.7"]                 
                 [cascalog-more-taps "0.2.0"]
                 [dwca-reader-clj "0.3.0-SNAPSHOT"]
                 [cartodb-clj "1.1.1-SNAPSHOT"]
                 [org.clojure/data.csv "0.1.2"]
                 [clj-http "0.4.3"]
                 [net.lingala.zip4j/zip4j "1.3.1"]
                 [com.google.guava/guava "12.0"]
                 [ratel/gdal "1.9.1"]]
  :dev-dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]
                     [midje-cascalog "0.4.0"]
                     [midje "1.4.0"]])
