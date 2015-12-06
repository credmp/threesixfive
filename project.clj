(defproject threesixfive "0.0.1"

  :description "ThreeSixFive"
  :url "https://www.github.com/credmp/threesixfive"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [clj-http "2.0.0"]
                 [org.clojure/data.json "0.2.6"]
                 [enlive "1.1.6"]
                 [clj-template "1.0.1"]
                 [selmer "0.9.5"]
                 [clj-time "0.9.0"]]

  :main ^:skip-aot threesixfive.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
