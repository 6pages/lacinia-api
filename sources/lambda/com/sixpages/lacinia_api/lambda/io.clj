(ns com.sixpages.lacinia-api.lambda.io
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as s]))

(defn key->keyword
  [key-string]
  (-> key-string
      (s/replace #"([a-z])([A-Z])" "$1-$2")
      (s/replace #"([A-Z]+)([A-Z])" "$1-$2")
      (s/lower-case)
      (keyword)))

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


(defn response-ring-to-api-gateway
  [r]
  (-> r
      (assoc
       :statusCode
       (:status r))
      (dissoc :status)
      (update
       :headers
       #(reduce-kv
         (fn [acc k v] (assoc acc (name k) v))
         {}
         %))
      (assoc :isBase64Encoded false)))
