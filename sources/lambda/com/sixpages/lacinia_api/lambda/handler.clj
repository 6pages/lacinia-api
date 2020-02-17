(ns com.sixpages.lacinia-api.lambda.handler

  (:gen-class
   :name com.sixpages.lacinia-api.lambda.handler
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  
  (:require [com.sixpages.lacinia-api.configuration :as configuration]
            [com.sixpages.lacinia-api.lambda.io :as io]
            [com.sixpages.lacinia-api.lambda.request :as request]
            [com.sixpages.lacinia-api.lambda.graphql :as graphql]
            [com.sixpages.lacinia-api.lambda.response :as response]
            [com.sixpages.lacinia-api.system :as system]))




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

    (if (not
         (request/correct-content-type? request-m))

      (response/content-type-error request-m)
      
      (->> request-m
           graphql/query
           (graphql/execute sys-m)
           response/build-ring
           response/ring-to-api-gateway
           (io/write-json output-stream)))))
