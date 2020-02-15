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

(defn build-response
  [sys-m request-m]
  (if (not
       (correct-content-type? request-m))

    {:status 404
     :body (str "request content-type needs to be 'application/graphql'."
                " Was '" (content-type request-m) "'")}
    
    (let [q (query request-m)]
      (execute sys-m q))))





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

    
    (let [r (build-response
             sys-m
             request-m)]

      (println "Response built --------")
      (clojure.pprint/pprint r)
      (println "-------------------------")
      
      (io/write-json
       output-stream
       (io/response-ring-to-api-gateway
        r)))))
