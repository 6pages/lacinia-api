(ns com.sixpages.lacinia-api.system
  (:require [com.stuartsierra.component :as component]
            [com.sixpages.app :as app]
            [com.sixpages.lacinia-api.resolver :as resolver]
            [com.sixpages.lacinia-api.schema :as schema]))


(defn new-system
  ([config]
   (new-system
    config
    (app/system-map config)
    app/resolver-component-keys))

  ([config extra-deps-m]
   (new-system
    config
    (merge
     (app/system-map config)
     extra-deps-m)
    app/resolver-component-keys))
  
  ([config
    extra-deps-m
    resolver-deps-ks]
   (merge
    (component/system-map
     :resolvers (component/using
                  (resolver/new-component config resolver-deps-ks)
                  resolver-deps-ks)
     :schema (component/using
               (schema/new-component config)
               [:resolvers]))
    extra-deps-m)))
