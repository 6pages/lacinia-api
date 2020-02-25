(ns com.sixpages.lacinia-api.pedestal
  (:require [com.stuartsierra.component :as component]
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
