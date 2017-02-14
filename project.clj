(defproject squid-mvc "0.1.0-SNAPSHOT"
  :description "An implementation of TodoMVC using the Squid framework."
  :url "https://github.com/logaan/squid-mvc"

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 #_[org.clojure/core.async "0.2.391"
                  :exclusions [org.clojure/tools.reader]]
                 [datascript "0.15.5"]
                 [alandipert/storage-atom "1.2.4"]
                 [bidi "2.0.16"]]

  :plugins [[lein-figwheel "0.5.8"]
            [lein-cljsbuild "1.1.4" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]
                :figwheel {:on-jsload "squid-mvc.core/on-js-reload"
                           :open-urls ["http://localhost:3449/index.html"]}

                :compiler {:main squid-mvc.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/squid_mvc.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :preloads [devtools.preload]}}
               {:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/squid_mvc.js"
                           :main squid-mvc.core
                           :externs ["domvm-externs.js"]
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.8.2"]
                                  [figwheel-sidecar "0.5.8"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:init (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

)
