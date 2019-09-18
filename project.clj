(defproject api-lacinia-pedestal-component "0.0.1-SNAPSHOT"
  :description "Lacinia Pedestal Component API"
  :url "http://github.com/adamtait/6pages.com"

  :min-lein-version "2.0.0"

  :dependencies [[org.clojure/clojure "1.10.1"]

                 ;; logging
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]
                 [io.pedestal/pedestal.log "0.5.7"]

                 ;; libraries
                 [com.stuartsierra/component "0.4.0"]

                 ;; pedestal (web server) & lacinia (graphql)
                 [io.pedestal/pedestal.service "0.5.7"]
                 [io.pedestal/pedestal.jetty "0.5.7"]
                 [com.walmartlabs/lacinia-pedestal "0.12.0"]]
  
  :resource-paths ["config", "resources"]

  :profiles {:dev
             {:source-paths ["dev" "dev-resources"]
              :dependencies [
                             [io.pedestal/pedestal.service-tools "0.5.7"]
                             [clj-http "3.10.0"]]}
             :uberjar {:aot [com.sixpages.api-lacinia-pedestal-component.system]}}
  :main ^{:skip-aot true} com.sixpages.api-lacinia-pedestal-component.system)
