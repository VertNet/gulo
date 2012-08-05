(defproject gulo "0.1.0-SNAPSHOT"
  :description "Shredding Darwin Core Archives with ferocity, strength, and Cascalog."
  :repositories {"conjars" "http://conjars.org/repo/"
                 "gbif" "http://repository.gbif.org/content/groups/gbif/"
                 "maven2" "http://repo2.maven.org/maven2"}
  :source-paths ["src/clj"]
  :source-path "src/clj"
  :resources-path "resources"
  :dev-resources-path "dev"
  :jvm-opts ["-XX:MaxPermSize=256M"
             "-XX:+UseConcMarkSweepGC"
             "-Xms1024M" "-Xmx1048M" "-server"]
  :plugins [[lein-swank "1.4.4"]]
  :profiles {:dev {:dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]
                                  [midje-cascalog "0.4.0"]]}
             :plugins [[lein-midje "2.0.0-SNAPSHOT"]]}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [cascalog "1.9.0"]                 
                 [eighty-cascalog-more-taps "0.2.1"]
                 [cascading/cascading-hadoop "2.0.2-wip-324"]
                 [org.pingles/cascading.protobuf "0.0.1"]
                 [dwca-reader-clj "0.5.0-SNAPSHOT"]
                 [cartodb-clj "1.5.2"]
                 [org.clojure/data.csv "0.1.2"]
                 [clj-http "0.4.3"]
                 [net.lingala.zip4j/zip4j "1.3.1"]
                 [com.google.guava/guava "12.0"]
                 [ratel/gdal "1.9.1"]]
  :min-lein-version "2.0.0"
  :aot [gulo.main])
