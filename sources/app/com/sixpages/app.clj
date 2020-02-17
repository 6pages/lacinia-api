(ns com.sixpages.app
  (:require [com.stuartsierra.component :as component]
            [com.sixpages.api.hello-example.resolver.get-hello :as get-hello]))


;;
;;  IMPORTANT!
;;
;;   All your components need (resolver or otherwise) need to defined
;;   here.



;;
;; system-map
;;
;;  single place to keep track of all your system components
;;  define all components needed for your lacinia-api based system here
;;
;;  [config]: configuration map loaded for the current environment.
;;  See resources/configuration

(defn system-map
  [config]
  {:get-hello            (get-hello/new-component config)})


;;
;; resolver-component-keys
;;
;;  list of keys for components in system-map (above) that are used as
;;  lacinia/graph-ql resolvers.
;;  *every* ":resolve" key-value in resources/schema.edn should be in this
;;  list (thus also in system-map)

(def resolver-component-keys
  [:get-hello])
