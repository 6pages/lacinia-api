(ns com.sixpages.api-lacinia-pedestal-component.handler

  (:gen-class
   :name com.sixpages.api-lacinia-pedestal-component.handler
   :implements [com.sixpages.api-lacinia-pedestal-component.handler])
  
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.sixpages.api-lacinia-pedestal-component.system :as system]))

(def load-config
  (memoize
   (fn []
     (some-> "configuration.edn"
             io/resource
             slurp
             edn/read-string))))


(defn -handleRequest
  [this is os context]
  (let [request (io/reader is)]

    (println "Request received --------")
    (clojure.pprint/pprint request)
    (println "-------------------------")
    
    ;; respond to HTTP request
    (let [w (io/writer os)]
      (.write w "RESPONSE")
      (.flush w))))
