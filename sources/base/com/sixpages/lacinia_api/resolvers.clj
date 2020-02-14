(ns com.sixpages.lacinia-api.resolvers
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.schema :as lacinia-schema]
            [com.walmartlabs.lacinia.util :as util]))




(defn get-hello
  [context args value]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello."})


(def build
  (memoize
   (fn [component]
     {:get-hello get-hello})))



;; Component

(defrecord ResolversComponent
    [config]
  component/Lifecycle

  (start [this])

  (stop [this]))


(defn new-component
  [config]
  (map->ResolversComponent {}))
