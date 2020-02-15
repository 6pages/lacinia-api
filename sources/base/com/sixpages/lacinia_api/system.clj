(ns com.sixpages.lacinia-api.system
  (:require [com.stuartsierra.component :as component]
            [com.sixpages.lacinia-api.resolvers :as resolvers]
            [com.sixpages.lacinia-api.schema :as schema]))



(def ^:dynamic *system*
  nil)

(defn new-system
  ([config]
   (new-system config {}))
  
  ([config extra-deps-m]
   (merge
    (component/system-map
     :resolvers (resolvers/new-component config)
     :schema (component/using
               (schema/new-component config)
               [:resolvers]))
    extra-deps-m)))

(defn get-system

  ([config]
   (get-system config {}))

  ([config extra-deps-m]
   (when-not *system*
     (alter-var-root
      #'*system*
      (constantly
       (component/start
        (new-system
         config
         extra-deps-m)))))
   *system*))
