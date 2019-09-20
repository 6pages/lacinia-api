(ns user
  (:require [clj-http.client :as http-client]
            [clojure.data.json :as json]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [com.stuartsierra.component :as component]
            [com.sixpages.api-lacinia-pedestal-component.server :as server]
            [com.sixpages.api-lacinia-pedestal-component.system :as system]))


;; system & component REPL refresh

(def user-system nil)

(defn new-system []
  (let [config (load-config)
        server (server/new-component config)]
    (-> config
        system/new-system
        (assoc
         :server (component/using
                   server
                   [:service])))))

(defn init []
  (alter-var-root
   #'user-system
   (constantly (new-system))))

(defn start []
  (alter-var-root
   #'user-system
   component/start)
  :ok)

(defn stop []
  (alter-var-root
   #'user-system
   #(when %
      (component/stop %)))
  :ok)

(defn go []
  (init)
  (start)
  :ready)

(defn reset []
  (stop)
  (refresh)
  (clojure.tools.namespace.reload/remove-lib 'user)
  (require 'user :reload)
  (refresh :after 'user/go))






;; Basic tests

(defn get-endpoint []
  (let [config (config-by-env-k env-k)
        {:keys [protocol host path port]} (:endpoint config)
        s (str protocol "://" host)]
    (cond-> s
      port (str ":" port)
      path (str "/" path))))


;; curl localhost:8888/graphql -X POST -H "Content-Type: application/graphql" -d "{ content(slug: \"/brief-template\") }"

(defn request-content
  [env]
  (http-client/post
   (get-endpoint)
   {:headers {"Content-Type" "application/graphql"}
    :body "{ hello() }"}))
