(ns com.sixpages.api-lacinia-pedestal-component.handler

  (:gen-class
   :name com.sixpages.api-lacinia-pedestal-component.handler
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  
  (:require [clojure.edn :as edn]
            [com.stuartsierra.component :as component]
            [com.sixpages.api-lacinia-pedestal-component.configuration :as configuration]
            [com.sixpages.api-lacinia-pedestal-component.io :as io]
            [com.sixpages.api-lacinia-pedestal-component.system :as system]))



(defn -handleRequest
  [this
   input-stream
   output-stream
   context]
  (let [config (configuration/load-m)
        sys (system/get-system config)
        request-m (io/read-m input-stream)]

    (println "Request received --------")
    (clojure.pprint/pprint request-m)
    (println "-------------------------")
    
    (io/write-json
     {:statusCode 200
      :body "Thanks for using com.sixpages.api-lacinia-pedestal-component"
      :isBase64Encoded false})))
