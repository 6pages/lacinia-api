(ns com.sixpages.configuration
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn get-env-var
  [name]
  (System/getenv name))

(defn assoc-env-vars
  [m]
  (reduce-kv
   (fn [acc var-name-k config-path]
     (assoc-in
      acc
      config-path
      (get-env-var (name var-name-k))))
   m
   (:environment-variable-to-configuration-path m)))

(def load-m
  "Load configuration (as map)"
  (memoize
   (fn []
     (some->
      "configuration.edn"
      io/resource
      slurp
      edn/read-string
      assoc-env-vars))))
