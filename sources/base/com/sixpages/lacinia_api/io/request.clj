(ns com.sixpages.lacinia-api.io.request)


;;
;; content-type

(defn content-type
  [request-m]
  (get-in
   request-m
   [:headers
    :content-type]))

(defn correct-content-type?
  [request-m]
  (=
   "application/graphql"
   (content-type request-m)))
