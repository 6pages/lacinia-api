(ns dev
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application.

  Call `(reset)` to reload modified code and (re)start the system.

  The system under development is `system`, referred from
  `com.stuartsierra.component.repl/system`.

  See also https://github.com/stuartsierra/component.repl"
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.set :as set]
   [clojure.string :as string]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer [refresh refresh-all clear]]
   [com.stuartsierra.component :as component]
   [com.stuartsierra.component.repl :refer [reset set-init start stop system]]

   [clj-http.client :as http-client]
   [clojure.data.json :as json]
   [com.sixpages.lacinia-api.configuration :as configuration]
   [com.sixpages.lacinia-api.lambda.resolve :as lambda-resolve]
   [com.sixpages.lacinia-api.pedestal.entry :as pedestal-entry]
   [com.sixpages.lacinia-api.system :as system]))


;; Do not try to load source code from 'resources' directory
(clojure.tools.namespace.repl/set-refresh-dirs "sources")



;;
;; system builders

(defmulti new-system
  (fn [config] (:system-type config)))

(defmethod new-system :pedestal
  [config]
  (system/new-system
   config
   (pedestal-entry/system-map config)))

(defmethod new-system :default
  [config]
  (system/new-system config))




;;
;; system management interfaces

(set-init
 (fn [_]
   (new-system
    (assoc
     (configuration/load-m)
     :system-type :lambda))))





;; Lambda tests

(defn lambda-request
  [body]
  {:headers
   {:content-type "application/graphql"}
   :body body})

(defn lambda-hello-test
  [sys]
  (->> "{ hello }"
       lambda-request
       lambda-resolve/query
       (lambda-resolve/execute sys)))





;; Pedestal tests

(def config-by-env-k
  {:dev (configuration/load-m)
   :alt {:endpoint {:protocol "https"
                    :host "localhost"
                    :path "dev"}}})

(defn get-endpoint [env-k]
  (let [config (config-by-env-k env-k)
        {:keys [protocol host path port]} (:endpoint config)
        s (str protocol "://" host)]
    (cond-> s
      port (str ":" port)
      path (str "/" path))))


;; curl localhost:8888/graphql -X POST -H "Content-Type: application/graphql" -d "{ content(slug: \"/brief-template\") }"

(defn pedestal-request
  []
  {:headers {"Content-Type" "application/graphql"}
   :body "{ hello }"})

(defn request-content
  [env]
  (http-client/post
   (get-endpoint)
   (pedestal-request)))
