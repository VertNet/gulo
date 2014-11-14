(defproject gulo "0.2.0-SNAPSHOT"
  :description "Darwin Core Archive harvester including wrapper for the GBIF Darwin Core Archive Reader library."
  :repositories {"conjars" "http://conjars.org/repo/",
                 "gbif" "http://repository.gbif.org/content/groups/gbif/",
                 "zip4j" "http://mvnrepository.com/artifact/",
                 "maven2" "http://repo2.maven.org/maven2"}
  :source-paths ["src/clj"]
  :java-source-paths ["src/jvm"]
  :dev-resources-paths ["dev"]
  :resources-path "resources"
  :dev-resources-path "dev"
  :jvm-opts ["-XX:MaxPermSize=256M"
             "-XX:+UseConcMarkSweepGC"
             "-Xms1024M"
             "-Xmx14336M" 
             "-server"]
  :profiles {:dev 
             {:resource-paths ["dev"],
              :dependencies [[midje "1.4.0"]
                             [org.apache.hadoop/hadoop-core "0.20.2-dev"
                                   :exclusions [org.slf4j/slf4j-log4j12]]
                             [cascalog/midje-cascalog "1.10.1-SNAPSHOT"]]
              :plugins [[lein-swank "1.4.4"]
                        [lein-midje "1.0.8"]
                        [lein-clojars "0.9.0"]]}}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [cascalog "1.10.0"]
                 [backtype/dfs-datastores "1.2.0"]
                 [cascalog-more-taps "0.3.1-SNAPSHOT"]
                 [backtype/dfs-datastores-cascading "1.3.0"]                 
                 [cascading/cascading-hadoop "2.2.0-wip-19" :exclusions [org.slf4j/slf4j-log4j12]]
                 [org.pingles/cascading.protobuf "0.0.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.gbif/dwca-reader "1.19-SNAPSHOT"]
                 [org.gbif/gbif-common "0.16"]
                 [dwca-reader-clj "0.8.0-SNAPSHOT"]
                 [org.clojure/data.json "0.2.1"]
                 [cartodb-clj "1.5.5"]
                 [org.clojure/data.csv "0.1.2"]
                 [clj-http "0.5.7"]
                 [net.lingala.zip4j/zip4j "1.3.1"]
                 [com.google.guava/guava "12.0"]
                 [org.gbif/gbif-metadata-profile "1.1-SNAPSHOT"]
                 [enlive "1.0.1"]
                 [org.apache.thrift/libthrift "0.8.0"]
                 [clj-time "0.3.4"]
                 [clj-aws-s3 "0.3.6"]
                 [org.clojure/java.jdbc "0.3.0-alpha1"]]
  :min-lein-version "2.0.0")
  ;;:aot [vn.schema gulo.hadoop.pail org.json])
