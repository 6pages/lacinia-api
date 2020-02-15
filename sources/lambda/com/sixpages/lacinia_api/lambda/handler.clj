(ns com.sixpages.lacinia-api.lambda.handler

  (:gen-class
   :name com.sixpages.lacinia-api.lambda.handler
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  
  (:require [clojure.edn :as edn]
            [com.stuartsierra.component :as component]
            [com.sixpages.lacinia-api.configuration :as configuration]
            [com.sixpages.lacinia-api.lambda.io :as io]
            [com.sixpages.lacinia-api.schema :as schema]
            [com.sixpages.lacinia-api.system :as system]
            [com.walmartlabs.lacinia :as lacinia]))


;;
;; validations

(defn content-type
  [request-m]
  (get-in
             request-m
             [:headers
              :content-type]))

(defn correct-content-type?
  [request-m]
  (=
   "application/graphql"
   (content-type request-m)))



;;
;; lacinia helpers

(defn query
  [request-m]
  (:body request-m))

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


;;
;; response

(defn resolve-query
  [sys-m request-m]
  (if (not
       (correct-content-type? request-m))

    {:status 404
     :body (str "request content-type needs to be 'application/graphql'."
                " Was '" (content-type request-m) "'")}
    
    (let [q (query request-m)]
      (execute sys-m q))))

(defn build-response
  [query-results]
  (reduce-kv
   (fn [response result-k result-m]
     (merge
      response
      (edn/read-string
       result-m)))
   {}
   (:data query-results)))




;;
;;  RequestStreamHandler. handleRequest
;;    main entry point for AWS Lambda requests

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

    
    (let [result (resolve-query
             sys-m
             request-m)]

      (println "Query resolved --------")
      (clojure.pprint/pprint result)
      (println "-------------------------")
      
      (io/write-json
       output-stream
       (io/response-ring-to-api-gateway
        (build-response result))))))
