(ns com.sixpages.lacinia-api.schema
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.sixpages.lacinia-api.resolvers :as resolvers]
            [com.walmartlabs.lacinia.schema :as lacinia-schema]
            [com.walmartlabs.lacinia.util :as util]))


(def build
  (memoize
   (fn [component]
     (-> (:schema-file-name component)
         io/resource
         slurp
         edn/read-string
         (util/attach-resolvers
          (resolvers/build
           (:resolvers component)))
         lacinia-schema/compile))))



;; Component

(defrecord SchemaComponent
    [config resolvers]
  component/Lifecycle

  (start [this]
    (assoc
     this
     :schema-file-name
     (str
      (name
       (get-in config [:schema :name]))
      ".edn")))

  (stop [this]))


(defn new-component
  [config]
  (map->SchemaComponent
   {:config
    (select-keys
     config
     [:schema])}))
