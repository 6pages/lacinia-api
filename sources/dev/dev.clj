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
   [clojure.tools.namespace.repl :refer [refresh refresh-all clear set-refresh-dirs]]
   [com.stuartsierra.component :as component]
   [com.stuartsierra.component.repl :refer [reset set-init start stop system]]

   [clj-http.client :as http-client]
   [clojure.data.json :as json]
   
   [com.sixpages.configuration :as configuration]
   [com.sixpages.lacinia-api.graphql :as graphql]
   [com.sixpages.lacinia-api.io.response :as response]
   [com.sixpages.lacinia-api.pedestal :as pedestal]
   [com.sixpages.lacinia-api.system :as api-system]
   
   [com.sixpages.generate :as gen]
   [com.sixpages.db.customer-store :as customer-store]
   [com.sixpages.db.user-store :as user-store]
   [com.stripe.coupon :as stripe-coupon]))


;; Do not try to load source code from 'resources' directory
(set-refresh-dirs
 "sources/base"
 "sources/pedestal"
 "sources/app"
 "sources/dev")


;;
;; system management interfaces

(set-init
 (fn [_]
   (let [config (configuration/load-m)]
     (api-system/new-system 
      config
      (pedestal/server-components config)))))





;;
;; execute interface

(defmulti execute
  (fn [server-type _] server-type))

(defmethod execute :lambda
  [_ request]
  (->> request
       graphql/query
       (graphql/execute system)
       response/build-ring
       response/ring-to-api-gateway))


(def get-endpoint
  (memoize
   (fn []
     (let [config (configuration/load-m)
           {:keys [protocol host path port]} (:endpoint config)
           s (str protocol "://" host)]
       (cond-> s
         port (str ":" port)
         path (str "/" path))))))

(defmethod execute :pedestal
  [_ request]
  (http-client/post
   (get-endpoint)
   request))

;; curl localhost:8888/graphql -X POST -H "Content-Type: application/graphql" -d "{ hello }"






;;
;; request helpers

(defn build-request
  [body]
  {:headers
   {:content-type "application/graphql"}
   :body body})

(defn response-body->map
  [response]
  (-> response
      :body
      (json/read-str :key-fn keyword)))



(defn hello-request
  []
  (build-request "{ hello }"))

(defn subscription-request
  [email payment-method]
  (let [proj-fields ["id" "latest_invoice"]
        proj (clojure.string/join " " proj-fields)]
    (build-request
     (str "mutation { newSubscription( "
          "email: \"" email "\"" ", "
          "payment_method: \"" payment-method "\""
          " )"
          "{ " proj " }"
          " }"))))

(defn subscription-coupon-request
  [email payment-method coupon-id]
  (let [proj-fields ["id"]
        proj (clojure.string/join " " proj-fields)]
    (build-request
     (str "mutation { newSubscription( "
          "email: \"" email "\"" ", "
          "payment_method: \"" payment-method "\", "
          "coupon_id: \"" coupon-id "\", "
          " )"
          "{ " proj " }"
          " }"))))

(defn promo-code-request
  [id]
  (let [proj-fields ["name" "valid" "amount_off" "percent_off"]
        proj (clojure.string/join " " proj-fields)
        req (str "{ promoCode( id: \"" id "\" ) "
                 "{ " proj " }"
                 " }")]
    (build-request req)))

(defn user-request
  [id]
  (let [proj-fields ["name" "email" "created_epoch_millis"]
        proj (clojure.string/join " " proj-fields)
        req (str "{ user( email: \"" id "\" ) "
                 "{ " proj " }"
                 " }")]
    (build-request req)))

(defn user-mutation-request
  [first-name last-name email password]
  (println "WARNING: THIS CHANGES THE USER TABLE IN THE DATABASE")
  (let [proj-fields ["id" "created_epoch_millis" "first_name" "last_name" "email"]
        proj (clojure.string/join " " proj-fields)
        req (str "mutation { user( "
                 "first_name: \"" first-name "\"" ", "
                 "last_name: \"" last-name "\"" ", "
                 "email: \"" email "\"" ", "
                 "password: \"" password "\"" ", "
                 " ) "
                 "{ " proj " }"
                 " }")]
    (build-request req)))





;; user database

(defn load-user
  [sys user-id]
  (user-store/retrieve
   (:user-store sys)
   user-id))

(defn load-user-by-email
  [sys email]
  (user-store/retrieve-by-email
   (:user-store sys)
   email))
