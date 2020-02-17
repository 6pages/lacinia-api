(ns com.sixpages.lacinia-api.lambda.response
  (:require [clojure.data.json :as json]
            [com.sixpages.lacinia-api.lambda.request :as request]))


;;
;; helpers

(defn map-keys
  ([f]
   (fn [m]
     (reduce-kv
      (fn [acc k v]
        (assoc acc (f k) v))
      {}
      m)))
  
  ([f m]
   ((map-keys f) m)))


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
