(ns user
  (:require [clj-http.client :as http-client]
            [clojure.data.json :as json]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [com.stuartsierra.component :as component]
            [com.sixpages.lacinia-api.configuration :as configuration]
            [com.sixpages.lacinia-api.lambda.handler :as lambda-handler]
            [com.sixpages.lacinia-api.pedestal.entry :as pedestal-entry]
            [com.sixpages.lacinia-api.system :as system]))


;;
;; system & component REPL refresh

(def ^:dynamic user-system
  nil)


;;
;; system builders

(defmulti new-system
  (fn [config] (:system-type config)))

(defmethod new-system :lambda
  [config]
  (system/new-system config))

(defmethod new-system :pedestal
  [config]
  (system/new-system
   config
   (pedestal-entry/system-map config)))




;;
;; system management interfaces

(defn init
  ([]
   (init :lambda))
  ([system-type]
   (alter-var-root
    #'user-system
    (constantly
     (new-system
      (assoc
       (configuration/load-m)
       :system-type
       system-type))))))

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

(defn go
  ([]
   (go :lambda))
  
  ([system-type]
   (init system-type)
   (start)
   :ready))

(defn reset []
  (stop)
  (refresh)
  (clojure.tools.namespace.reload/remove-lib 'user)
  (require 'user :reload)
  (refresh :after 'user/go))







;; Lambda tests

(defn lambda-request
  []
  {:headers
   {:content-type "application/graphql"}
   :body
   "{ hello }"})

(defn lambda-test
  [sys]
  (->> (lambda-request)
       (lambda-handler/resolve-query sys)
       lambda-handler/build-response))




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
