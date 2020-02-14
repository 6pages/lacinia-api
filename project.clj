(defproject api-lacinia-pedestal-component "0.0.1-SNAPSHOT"
  :description "Lacinia Pedestal Component API"
  :url "http://github.com/adamtait/lacinia-pedestal-component"

  :min-lein-version "2.0.0"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "0.4.500"]

                 ;; pedestal (web server)
                 [io.pedestal/pedestal.service "0.5.7"
                  :exclusions  [org.clojure/tools.analyzer.jvm
                                org.clojure/core.async]]

                 ;; lacinia (graphql)
                 #_[org.antlr/antlr4 "4.7.2"]
                 #_[org.antlr/antlr4-runtime "4.7.2"]
                 #_[clj-antlr "0.2.5"
                  :exclusions [org.antlr/antlr4
                               org.antlr/antlr4-runtime]]
                 [com.walmartlabs/lacinia "0.36.0"
                  ;;:exclusions [clj-antlr]
                  ]
                 [com.walmartlabs/lacinia-pedestal "0.13.0"
                  :exclusions [io.pedestal/pedestal.service
                               io.pedestal/pedestal.jetty
                               com.walmartlabs/lacinia]]

                 ;; libraries
                 [com.stuartsierra/component "0.4.0"]]
  
  :resource-paths ["config", "resources"]

  :profiles {:dev
             {:source-paths ["dev" "dev-resources"]
              :dependencies [
                             [io.pedestal/pedestal.jetty "0.5.7"
                              :exclusions [org.eclipse.jetty/jetty-util]]
                             [org.eclipse.jetty/jetty-util "9.4.20.v20190813"]
                             [io.pedestal/pedestal.service-tools "0.5.7"]
                             [clj-http "3.10.0"]]}
             :log
             {:dependencies [
                             [ch.qos.logback/logback-classic "1.2.3"
                              :exclusions [org.slf4j/slf4j-api]]
                             [org.slf4j/jul-to-slf4j "1.7.26"]
                             [org.slf4j/jcl-over-slf4j "1.7.26"]
                             [org.slf4j/log4j-over-slf4j "1.7.26"]
                             [io.pedestal/pedestal.log "0.5.7"]]}
             :uberjar {:aot [com.sixpages.api-lacinia-pedestal-component.handler]}})
