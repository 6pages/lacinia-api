(ns com.sixpages.lacinia-api.entry
  (:gen-class)
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]))



(defn -main
  [& args]
  (let [config (configuration/load-m)]
    (system/get-system config)))
