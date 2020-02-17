(ns com.sixpages.lacinia-api.lambda.handler

  (:gen-class
   :name com.sixpages.lacinia-api.lambda.handler
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  
  (:require [com.stuartsierra.component :as component]
            [com.sixpages.lacinia-api.configuration :as configuration]
            [com.sixpages.lacinia-api.lambda.io :as io]
            [com.sixpages.lacinia-api.lambda.resolve :as resolve]
            [com.sixpages.lacinia-api.system :as system]))


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

(defn content-type-error-response
  [request-m]
  {:status 404
   :body (str "request content-type needs to be 'application/graphql'."
              " Was '" (content-type request-m) "'")})




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
         (correct-content-type? request-m))

      (content-type-error-response request-m)
      
      (->> request-m
           resolve/query
           (resolve/execute sys-m)
           io/build-ring-response
           io/ring-to-api-gateway-response
           (io/write-json output-stream)))))
