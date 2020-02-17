(ns com.sixpages.lacinia-api.lambda.io
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as s]))

;;
;; helpers

(defn key->keyword
  [key-string]
  (-> key-string
      (s/replace #"([a-z])([A-Z])" "$1-$2")
      (s/replace #"([A-Z]+)([A-Z])" "$1-$2")
      (s/lower-case)
      (keyword)))

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
;; stream reader & writer

(defn read-m
  [input-stream]
  (-> input-stream
      io/reader 
      (json/read :key-fn key->keyword)))

(defn write-json
  [output-stream response]
  (let [w (io/writer output-stream)]
    (json/write
     response
     w)
    (.flush w)))



;;
;; api gateway response

(defn response-ring-to-api-gateway
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
