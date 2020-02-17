(ns com.sixpages.api.hello-example.resolver.get-hello
  (:require [com.sixpages.lacinia-api.resolver :as resolver]
            [com.stuartsierra.component :as component]))



;; Component

(defrecord GetHelloComponent
    [config]
  component/Lifecycle

  (start [this]
    this)

  (stop [this]
    this)

  resolver/Resolver
  (execute
    [this context args value]
    (get-in
     this
     [:config :body])))


(defn new-component
  [config]
  (map->GetHelloComponent
   {:config
    (get-in
     config
     [:schema
      :resolvers
      :get-hello])}))
