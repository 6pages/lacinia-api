(ns com.sixpages.lacinia-api.pedestal.entry
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [com.sixpages.configuration :as configuration]
            [com.sixpages.lacinia-api.pedestal :as pedestal]))
            [com.sixpages.lacinia-api.system :as system]))


;;
;; system var

(def ^:dynamic *system*
  nil)


;;
;; get-system
;;   if (= *system* nil); creates & starts a new system
;;   else, returns already running system

(defn get-system
  []
  (when-not *system*
    (let [config (configuration/load-m)
          sys (system/new-system
               config
               (pedestal/server-components config)]
      (alter-var-root
       #'*system*
       (constantly
        (component/start sys)))))
  *system*)



;;
;; -main entry

(defn -main
  [& args]
  (get-system))
