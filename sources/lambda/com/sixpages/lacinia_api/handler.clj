(ns com.sixpages.lacinia-api.handler

  (:gen-class
   :name com.sixpages.lacinia-api.handler
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  
  (:require [clojure.data.json :as json]
            [clojure.edn :as edn]
            [com.stuartsierra.component :as component]
            [com.sixpages.lacinia-api.configuration :as configuration]
            [com.sixpages.lacinia-api.io :as io]
            [com.sixpages.lacinia-api.schema :as schema]
            [com.sixpages.lacinia-api.system :as system]
            [com.walmartlabs.lacinia :as lacinia]))

(defn query
  [request-m]
  (json/read-str
   (:body request-m)
   :key-fn keyword))

(defn execute
  [sys-m query-m]
  (let [sc (schema/build
            (:schema sys-m))]
    (lacinia/execute
     sc
     query-m
     {}    ;; variables
     nil   ;; context
     )))


(defn -handleRequest
  [this
   input-stream
   output-stream
   context]

  (let [config (configuration/load-m)
        sys-m (system/get-system config)
        request-m (io/read-m input-stream)]

    (println "Request received --------")
    (clojure.pprint/pprint request-m)
    (println "-------------------------")

    (let [response {:status 200
                    :body "Thanks for using com.sixpages.lacinia-api"}
          q (query request-m)
          r (execute sys-m q)]

      (println "Response built --------")
      (println q)
      (clojure.pprint/pprint r)
      (println "-------------------------")
      
      (io/write-json
       output-stream
       (io/response-ring-to-api-gateway
        r)))))
