(ns com.sixpages.lacinia-api.resolver.components
  (:require [com.sixpages.lacinia-api.resolver.get-hello :as get-hello]))



;;
;; all
;;  single place to keep track of all your resolver components
;;  referenced anywhere that systems are built

(defn all
  [config]
  {:get-hello (get-hello/new-component config)})

