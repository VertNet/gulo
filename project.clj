(defproject gulo "0.1.0-SNAPSHOT"
  :description "Shredding Darwin Core Archives with ferocity, strength, and Cascalog."
  :repositories {"conjars" "http://conjars.org/repo/"
                 "gbif" "http://repository.gbif.org/content/groups/gbif/"
                 "maven2" "http://repo2.maven.org/maven2"}
  :source-paths ["src/clj"]
  :java-source-paths ["src/jvm"]
  :dev-resources-paths ["dev"]
  :resources-path "resources"
  :dev-resources-path "dev"
  :jvm-opts ["-XX:MaxPermSize=256M"
             "-XX:+UseConcMarkSweepGC"
             "-Xms1024M" "-Xmx1048M" "-server"]
  :profiles {:dev {:dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev" :exclusions [org.slf4j/slf4j-log4j12]]
                                  [cascalog/midje-cascalog "1.10.1-SNAPSHOT"]]
                   :plugins [[lein-midje "3.0-beta1"]]}}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [cascalog "1.10.0"]
                 [backtype/dfs-datastores "1.2.0"]
                 [cascalog-more-taps "0.3.1-SNAPSHOT"]
                 [backtype/dfs-datastores-cascading "1.3.0"]                 
                 [cascading/cascading-hadoop "2.2.0-wip-19" :exclusions [org.slf4j/slf4j-log4j12]]
                 [org.pingles/cascading.protobuf "0.0.1"]
                 [dwca-reader-clj "0.7.0-SNAPSHOT"]
                 [org.clojure/data.json "0.2.1"]
                 [cartodb-clj "1.5.3"]
                 [org.clojure/data.csv "0.1.2"]
                 [clj-http "0.4.3"]
                 [net.lingala.zip4j/zip4j "1.3.1"]
                 [com.google.guava/guava "12.0"]
                 ;;[ratel/gdal "1.9.1"]
                 ;;[clj-aws-s3 "0.3.2"]
                 [org.gbif/gbif-metadata-profile "1.1-SNAPSHOT"]
                 [enlive "1.0.1"]
                 [org.apache.thrift/libthrift "0.8.0"]]
  :min-lein-version "2.0.0"
  :aot [vn.schema gulo.hadoop.pail org.json])
