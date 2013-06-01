(defproject news_agregator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "1.1.5"]
                 [enlive "1.1.1"]
                 [korma "0.3.0-RC5"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [overtone/at-at "1.1.1"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler news_agregator.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})
