(ns com.sixpages.lacinia-api.pedestal.entry
  (:gen-class)
  (:require [com.sixpages.lacinia-api.configuration :as configuration]
            [com.sixpages.lacinia-api.pedestal.system :as psystem]
            [com.sixpages.lacinia-api.system :as system]))


;;
;; -main entry

(defn -main
  [& args]
  (let [config (configuration/load-m)]
    (system/get-system
     config
     (psystem/system-map config))))
