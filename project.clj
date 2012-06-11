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
                 [cascalog "1.9.0-wip8"]
                 [dwca-reader-clj "0.1.0-SNAPSHOT"]
                 [cartodb-clj "1.0.0-SNAPSHOT"]]
  :dev-dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]
                     [midje-cascalog "0.4.0"]
                     [midje "1.4.0"]])
