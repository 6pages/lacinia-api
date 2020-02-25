(ns com.sixpages.lacinia-api.io.response
  (:require [clojure.string :as s]
            [clojure.data.json :as json]
            [com.sixpages.lacinia-api.io.request :as request]
            [com.sixpages.lang :refer [map-keys]]))



;;
;; errors

(defn content-type-error
  [request-m]
  {:status 400
   :body (str "Request included header 'Content-Type': '"
              (request/content-type request-m)
              "'\nThis GraphQL-based service accepts only 'Content-Type': 'application/graphql'")})


;;
;; response builders

(defn build-ring
  [response-m]
  (let [headers {:content-type "application/json"}
        body (json/write-str response-m)]
    {:status  200
     :headers headers
     :body    body}))

(defn ring-to-api-gateway
  [r]
  (-> r
      (assoc :isBase64Encoded false)

      (assoc
       :statusCode
       (:status r))
      (dissoc :status)

      (update
       :headers
       (map-keys name))))
