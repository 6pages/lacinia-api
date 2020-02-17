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
   [com.sixpages.lacinia-api.lambda.request :as lambda-request]
   [com.sixpages.lacinia-api.lambda.graphql :as lambda-graphql]
   [com.sixpages.lacinia-api.lambda.response :as lambda-response]
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
;; com.stuartsierra.component.repl setup

(set-init
 (fn [_]
   (new-system
    (assoc
     (configuration/load-m)
     :system-type :lambda))))





;; helpers

(defn build-request
  [body]
  {:headers
   {:content-type "application/graphql"}
   :body body})





;; Lambda tests

(defn lambda-hello-test
  [sys]
  (->> "{ hello }"
       build-request
       lambda-graphql/query
       (lambda-graphql/execute sys)
       lambda-response/build-ring))



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


;; curl localhost:8888/graphql -X POST -H "Content-Type: application/graphql" -d "{ hello }"

(defn request-content
  [env]
  (http-client/post
   (get-endpoint)
   (build-request
    "{ hello }")))
