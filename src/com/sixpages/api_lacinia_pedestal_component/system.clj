(ns com.sixpages.api-lacinia-pedestal-component.system
  (:gen-class)
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.sixpages.api-lacinia-pedestal-component.service :as service]))



(def ^:dynamic *system*
  nil)

(defn new-system
  [config]
  (component/system-map
   :service (service/new-component config)))

(defn get-system
  [config]
  (when-not *system*
    (alter-var-root
     #'*system*
     (constantly
      (component/start
       (new-system config)))))
  *system*)



#_(defn -main
  [& args]
  (let [cfg (load-config)]
    (component/start
     (new-system cfg))))
