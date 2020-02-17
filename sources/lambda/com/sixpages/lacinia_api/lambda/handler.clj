(ns com.sixpages.lacinia-api.lambda.handler

  (:gen-class
   :name com.sixpages.lacinia-api.lambda.handler
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  
  (:require [clojure.data.json :as json]
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
  (let [compiled-schema (get-in sys-m [:schema :compiled])]
    (lacinia/execute
     compiled-schema
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
  (let [headers {:content-type "application/json"}
        body (json/write-str query-results)]
    {:status 200
     :headers headers
     :body body}))




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
      
      (->> result
           build-response
           io/response-ring-to-api-gateway
           (io/write-json output-stream)))))
