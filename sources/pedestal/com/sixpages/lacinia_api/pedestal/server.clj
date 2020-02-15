(ns com.sixpages.lacinia-api.pedestal.server
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]))

(defrecord Server
    [service config]
  component/Lifecycle

  (start [this]
    (if (:running-server this)
      this
      (do

        (let [port (get-in config [:io-pedestal-http :io.pedestal.http/port])]
          (println "Starting Server component on port" port))
        
        (let [service-map (get-in this [:service :service-map])
              server (-> service-map
                         http/create-server
                         http/start)]
          (assoc
           this
           :running-server server)))))

  (stop [this]
    (if-not (:running-server this)
      this
      (do
        (println "\nStopping server component.")
        (http/stop (:running-server this))
        (assoc this :running-server nil)))))

(defn new-component
  [config]
  (map->Server
   {:config
    (select-keys
     config
     [:io-pedestal-http])}))
