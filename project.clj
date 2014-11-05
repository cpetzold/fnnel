(defproject fnnel "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [om "0.7.1"]
                 [prismatic/plumbing "0.3.5"]
                 [prismatic/om-tools "0.3.4"]
                 [figwheel "0.1.5-SNAPSHOT"]
                 [pani "0.0.2"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [lein-figwheel "0.1.5-SNAPSHOT"]
            [lein-garden "0.2.1"]]

  :source-paths ["src"]

  :figwheel
  {:css-dirs ["resources/public/css"]}

  :garden
  {:builds
   [{:source-paths ["src/clj"]
     :stylesheet fnnel.css/screen
     :compiler {:output-to "resources/public/css/compiled/fnnel.css"
                :pretty-print? true}}]}

  :cljsbuild
  {:builds
   [{:source-paths ["src/cljs"]
     :compiler {:output-to "resources/public/js/compiled/fnnel.js"
                :output-dir "resources/public/js/compiled/out"
                :optimizations :none
                :source-map true}}]})
