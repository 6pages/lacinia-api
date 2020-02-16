(ns com.sixpages.lacinia-api.pedestal.entry
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [com.sixpages.lacinia-api.pedestal.server :as server]
            [com.sixpages.lacinia-api.pedestal.service :as service]
            [com.sixpages.lacinia-api.resolvers.get-hello :as get-hello]))

;;
;; resolvers

(defn resolver-components
  [config]
  {:get-hello (get-hello/new-component config)})


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


;;
;; -main entry

(defn -main
  [& args]
  (let [config (configuration/load-m)]
    (system/get-system
     config
     (resolver-components config)
     (server-components config))))
