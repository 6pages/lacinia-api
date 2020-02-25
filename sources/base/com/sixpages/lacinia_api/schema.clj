(ns com.sixpages.lacinia-api.schema
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.schema :as lacinia-schema]
            [com.walmartlabs.lacinia.util :as util]))


(defn build
  [file-name resolvers]
  (-> file-name 
      io/resource
      slurp
      edn/read-string
      (util/attach-resolvers resolvers)
      lacinia-schema/compile))



;; Component

(defrecord SchemaComponent
    [config resolvers]
  component/Lifecycle

  (start [this]
    (let [file-name (str
                     (name
                      (get-in config [:schema :name]))
                     ".edn")
          compiled (build
                    file-name
                    (:m resolvers))]
      (assoc
       this
       :file-name file-name
       :compiled compiled)))

  (stop [this]
    (dissoc
     this
     :file-name
     :compiled)))


(defn new-component
  [config]
  (map->SchemaComponent
   {:config
    (select-keys
     config
     [:schema])}))
