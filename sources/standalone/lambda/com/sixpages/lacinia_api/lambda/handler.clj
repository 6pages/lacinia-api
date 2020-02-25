(ns com.sixpages.lacinia-api.lambda.handler

  (:gen-class
   :name com.sixpages.lacinia-api.lambda.handler
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  
  (:require [com.stuartsierra.component :as component]
            [com.sixpages.configuration :as configuration]
            [com.sixpages.lacinia-api.graphql :as graphql]
            [com.sixpages.lacinia-api.io.request :as request]
            [com.sixpages.lacinia-api.io.response :as response]
            [com.sixpages.lacinia-api.lambda.io :as io]            
            [com.sixpages.lacinia-api.system :as system]))


;;
;; system var

(def ^:dynamic *system*
  nil)


;;
;; get-system
;;   if (= *system* nil); creates & starts a new system
;;   else, returns already running system

(defn get-system
  []
  (when-not *system*
    (let [config (configuration/load-m)
          system-map (system/new-system config)]
      (alter-var-root
       #'*system*
       (constantly
        (component/start system-map)))))
  *system*)



;;
;;  RequestStreamHandler. handleRequest
;;    main entry point for AWS Lambda requests

(defn -handleRequest
  [this
   input-stream
   output-stream
   context]

  (let [sys (get-system)
        request-m (io/read-m input-stream)]

    (if (not
         (request/correct-content-type? request-m))

      (response/content-type-error request-m)
      
      (->> request-m
           graphql/query
           (graphql/execute sys)
           response/build-ring
           response/ring-to-api-gateway
           (io/write-json output-stream)))))
