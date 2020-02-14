(ns com.sixpages.lacinia-api.handler

  (:gen-class
   :name com.sixpages.lacinia-api.handler
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  
  (:require [clojure.edn :as edn]
            [com.stuartsierra.component :as component]
            [com.sixpages.lacinia-api.configuration :as configuration]
            [com.sixpages.lacinia-api.io :as io]
            [com.sixpages.lacinia-api.system :as system]))



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
     output-stream
     {:statusCode 200
      :body "Thanks for using com.sixpages.lacinia-api"
      :isBase64Encoded false})))
