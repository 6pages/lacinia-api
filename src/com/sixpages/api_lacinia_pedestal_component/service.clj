(ns com.sixpages.api-lacinia-pedestal-component.service
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.pedestal :as lacinia-pedestal]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]
            [io.pedestal.interceptor :as interceptor]
            [io.pedestal.log :as log]
            [ring.util.response :as ring-response]))



;; Resolvers (GraphQL endpoints)

(defn get-hello
  [context args value]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello."})


;; Lacinia (GraphQL) Schema

(def resolvers
  {:get-hello get-hello})

(def compiled-schema
  "Match resources/schema.edn with resolvers"
  (-> "schema.edn"
      io/resource
      slurp
      edn/read-string
      (util/attach-resolvers resolvers)
      schema/compile))



;; Interceptors - log request, response

(def log-request-ks
  [:protocol :remote-addr :headers :content-length :content-type
   :path-info :uri :query-string :path-params :body :scheme
   :request-method :context-path])

(def log-request-response-interceptor
  (interceptor/interceptor
   {:name :log-request-response-interceptor
    :enter (fn [context]
             (println {:msg "New request"
                          :interceptor "log-request-response-interceptor"
                          :request
                          (select-keys
                           (:request context)
                           log-request-ks)})
             context)
    :leave (fn [context]
             (println {:msg "New response"
                          :interceptor "log-request-response-interceptor"
                          :response (:response context)})
             context)}))


;; Interceptors - handle exceptions

(defn- error-debug
  "When an error propagates to this interceptor error fn, trap it,
  print it to the output stream of the HTTP request, and do not
  rethrow it.
  Borrowed from Pedestal example."
  [{:keys [servlet-response] :as context} exception]

  (log/error :msg "::exception-debug interceptor caught an exception; Forwarding it as the response."
             :exception exception)

  (assoc context
         :response (-> (ring-response/response
                        (with-out-str (println "Error processing request!")
                          (println "Exception:\n")
                          (clojure.stacktrace/print-cause-trace exception)
                          (println "\nContext:\n")
                          (clojure.pprint/pprint context)))
                       (ring-response/status 500))))

(defn- error-production
  "When an error propagates to this interceptor error fn, trap it,
  log it, send the client a non-descript error message and do not
  rethrow it."
  [{:keys [servlet-response] :as context} exception]

  (log/error :msg "::error-production interceptor caught an exception. Sending HTTP/500."
             :exception exception)

  (assoc context
         :response {:status  500
                    :headers {}
                    :body    "Error."}))

(defn error-interceptor
  [env]
  (interceptor/interceptor
   (if (= env :dev)
     {:name ::exception-debug
      :error error-debug}
     {:name ::exception-production
      :error error-production})))



;; Interceptor stack

(defn interceptor-stack
  "Augments Lacinia Pedestal's default interceptors (See
  https://github.com/walmartlabs/lacinia-pedestal [pedestal.clj/default-interceptors])
  with our own."
  ([system schema]
   (interceptor-stack system schema {}))
  ([system schema
    {:keys [async] :or {async false}}]
   (let [system-components (-> system
                               (dissoc :io-pedestal-http)
                               (dissoc :lacinia-pedestal-service))
         system-m {::system system-components}]
     [log-request-response-interceptor
      lacinia-pedestal/json-response-interceptor
      lacinia-pedestal/graphql-data-interceptor
      lacinia-pedestal/status-conversion-interceptor
      lacinia-pedestal/missing-query-interceptor
      (lacinia-pedestal/query-parser-interceptor schema)
      lacinia-pedestal/disallow-subscriptions-interceptor
      lacinia-pedestal/prepare-query-interceptor

      ;; used for access to components from query resolvers
      (lacinia-pedestal/inject-app-context-interceptor system-m)
      
      (if async
        lacinia-pedestal/async-query-executor-handler
        lacinia-pedestal/query-executor-handler)])))

(defn interceptors
  "builds interceptor stack"
  [env system schema]
  (->> schema
       (interceptor-stack system)
       (cons (error-interceptor env))
       vec))



;; Lacinia Pedestal Service Map

(defn build-service-map
  "builds Lacinia Pedestal service map"
  [config schema interceptors]
  (let [{:keys [io-pedestal-http lacinia-pedestal-service]} config]
   (-> compiled-schema

       ;; add lacinia configuration and interceptor chain
       (lacinia-pedestal/service-map
        (assoc lacinia-pedestal-service
               :interceptors interceptors))

       ;; add pedestal configuration
       (merge io-pedestal-http)

       ;; allow all origins (for now)
       (assoc-in
        [:io.pedestal.http/allowed-origins :allowed-origins]
        (constantly true)))))



;; Service

(defrecord Service
    [config content-store]
  component/Lifecycle

  (start [this]
    (let [{:keys [io-pedestal-http]} config]

      ;; create pedestal lacinia service map
      (let [is (interceptors
                (:env io-pedestal-http)
                this
                compiled-schema)
            service-m (build-service-map
                       config
                       compiled-schema
                       is)]
        (assoc this
               :service-map service-m))))

  (stop [this]))


(defn new-component
  [config]
  (map->Service
   {:config (select-keys
             config
             [:io-pedestal-http
              :lacinia-pedestal-service])}))
