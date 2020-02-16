(ns com.sixpages.lacinia-api.resolver.get-hello
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

  resolver/ResolverComponent
  (resolve-request
    [this context args value]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (get-in
            this
            [:config :body])}))


(defn new-component
  [config]
  (map->GetHelloComponent
   {:config
    (get-in
     config
     [:schema
      :resolvers
      :get-hello])}))
