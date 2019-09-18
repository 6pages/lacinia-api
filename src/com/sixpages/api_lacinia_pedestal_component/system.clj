(ns com.sixpages.api-lacinia-pedestal-component.system
  (:gen-class)
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.sixpages.api-lacinia-pedestal-component.server :as server]
            [com.sixpages.api-lacinia-pedestal-component.service :as service]))

(defn new-system
  [config]
  (let [service (service/new-component config)]
    (component/system-map
     :service (component/using
                service
                [])
     :server (component/using
               (server/new-component config)
               [:service]))))


(def load-config
  (memoize
   (fn []
     (some-> "configuration.edn"
             io/resource
             slurp
             edn/read-string))))

(defn -main
  [& args]
  (let [cfg (load-config)]
    (component/start
     (new-system cfg))))
