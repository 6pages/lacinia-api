(ns com.sixpages.lang
  (:require [clojure.string :as s]))

(defn map-keys
  ([f]
   (fn [m]
     (reduce-kv
      (fn [acc k v]
        (assoc
         acc
         (f k)
         (if (coll? v)
           (if (map? v)
             (map-keys f v)
             (map #(map-keys f %) v []))
           v)))
      {}
      m)))
  
  ([f m]
   ((map-keys f) m)))


(defn ks-hyphens->underscores
  [m]
  (map-keys
   #(-> % name (s/replace #"-" "_") keyword)
   m))

(defn ks-underscores->hyphens
  [m]
  (map-keys
   #(-> % name (s/replace #"_" "-") keyword)
   m))
