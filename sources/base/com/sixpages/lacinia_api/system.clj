(ns com.sixpages.lacinia-api.system
  (:require [com.stuartsierra.component :as component]
            [com.sixpages.app :as app]
            [com.sixpages.lacinia-api.resolver :as resolver]
            [com.sixpages.lacinia-api.schema :as schema]))


;;
;; new system

(defn new-system
  ([config]
   (new-system config (app/system-map config)))

  ([config extra-deps-m]
   (new-system
    config
    extra-deps-m
    app/resolver-component-keys))
  
  ([config extra-deps-m resolver-deps-ks]
   (merge
    (component/system-map
     :resolvers (component/using
                  (resolver/new-component config resolver-deps-ks)
                  resolver-deps-ks)
     :schema (component/using
               (schema/new-component config)
               [:resolvers]))
    extra-deps-m)))


;;
;; system var

(def ^:dynamic *system*
  nil)


;;
;; get-system
;;   if (= *system* nil); creates & starts a new system
;;   else, returns already running system

(defn get-system

  ([config]
   (get-system config (app/system-map config)))

  ([config extra-deps-m]
   (get-system
    config
    extra-deps-m
    app/resolver-component-keys))
  
  ([config extra-deps-m resolver-deps-m]
   (when-not *system*
     (alter-var-root
      #'*system*
      (constantly
       (component/start
        (new-system
         config
         extra-deps-m
         resolver-deps-m)))))
   *system*))
