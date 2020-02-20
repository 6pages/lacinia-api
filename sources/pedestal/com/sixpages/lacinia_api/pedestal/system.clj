(ns com.sixpages.lacinia-api.pedestal.system
  (:require [com.stuartsierra.component :as component]
            [com.sixpages.app :as app]
            [com.sixpages.lacinia-api.pedestal.server :as server]
            [com.sixpages.lacinia-api.pedestal.service :as service]))

(defn server-components
  [config]
  {:server  (component/using
              (server/new-component config)
              [:service])
   :service (component/using
              (service/new-component config)
              [:schema])})

(defn system-map
  [config]
  (merge
   (app/system-map config)
   (server-components config)))
