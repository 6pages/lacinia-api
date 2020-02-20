(ns com.sixpages.lacinia-api.graphql
  (:require [com.walmartlabs.lacinia :as lacinia]))

(defn query
  [request-m]
  (:body request-m))

(defn compiled-schema
  [system-map]
  (get-in
   system-map
   [:schema :compiled]))

(defn execute
  [system-map query-m]
  (lacinia/execute
   (compiled-schema system-map)
   query-m
   {}    ;; variables
   nil   ;; context
   ))
