(ns com.sixpages.lacinia-api.resolver.components
  (:require [com.sixpages.lacinia-api.resolver.get-hello :as get-hello]))



;;
;; build
;;  single place to keep track of all your resolver components
;;  referenced anywhere that systems are built

(defn build
  [config]
  {:get-hello (get-hello/new-component config)})

