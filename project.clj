(defproject Handy "1.0.0-SNAPSHOT"
  :description "Self-documenting IRC bot"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [clj-http "0.1.3"]
                 [org.clojure/data.json "0.1.2"]
                 [clj-time "0.4.4"]
                 [org.ocpsoft.prettytime/prettytime "1.0.8.Final"]
                 [clojure-twitter "1.2.6-SNAPSHOT"]]
  :main Handy.core
  :jvm-opts ["-Xms32m"  "-Xmx64m"])
