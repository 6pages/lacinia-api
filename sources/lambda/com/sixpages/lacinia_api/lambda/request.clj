(ns com.sixpages.lacinia-api.lambda.request)


;;
;; validations

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
