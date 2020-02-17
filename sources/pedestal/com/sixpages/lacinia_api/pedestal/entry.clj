(ns com.sixpages.lacinia-api.pedestal.entry
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [com.sixpages.app :as app]
            [com.sixpages.lacinia-api.pedestal.server :as server]
            [com.sixpages.lacinia-api.pedestal.service :as service]))


;;
;; component deps

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



;;
;; -main entry

(defn -main
  [& args]
  (let [config (configuration/load-m)]
    (system/get-system
     config
     (system-map config))))
