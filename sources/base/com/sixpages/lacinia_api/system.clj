(ns com.sixpages.lacinia-api.system
  (:require [com.stuartsierra.component :as component]
            [com.sixpages.lacinia-api.resolver :as resolver]
            [com.sixpages.lacinia-api.schema :as schema]))


;;
;; new system

(defn new-system
  ([config]
   (new-system config {}))

  ([config resolver-deps-m]
   (new-system config resolver-deps-m {}))
  
  ([config resolver-deps-m extra-deps-m]
   (let [resolver-deps-ks (into []
                                (keys resolver-deps-m))]
     (merge
      (component/system-map
       :resolvers (component/using
                    (resolver/new-component config resolver-deps-ks)
                    resolver-deps-ks)
       :schema (component/using
                 (schema/new-component config)
                 [:resolvers]))
      extra-deps-m
      resolver-deps-m))))


;;
;; system var

(def ^:dynamic *system*
  nil)


;;
;; get-system
;;   -> you call this

(defn get-system

  ([config]
   (get-system config {}))

  ([config resolver-deps-m]
   (get-system config resolver-deps-m {}))
  
  ([config resolver-deps-m extra-deps-m]
   (when-not *system*
     (alter-var-root
      #'*system*
      (constantly
       (component/start
        (new-system
         config
         resolver-deps-m
         extra-deps-m)))))
   *system*))
