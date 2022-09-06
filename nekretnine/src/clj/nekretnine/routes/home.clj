(ns nekretnine.routes.home
  (:require
   [nekretnine.layout :as layout]
   [nekretnine.db.core :as db]
   [clojure.java.io :as io]
   [nekretnine.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [nekretnine.validation :refer [validate-adresa]]))




(defn adresa-list [_]
  (response/ok {:adrese (vec (db/get-adrese))}))



(defn save-adresa! [{:keys [params]}]
  (if-let [errors (validate-adresa params)]
    (response/bad-request {:errors errors})
    (try
      (db/save-adresa! params)
      (response/ok {:status :ok})
      (catch Exception e
        (response/internal-server-error
         {:errors {:server-error ["Neuspelo cuvanje"]}})))))


(defn home-page [request]
  (layout/render
   request
   "home.html"))

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/adrese" {:get adresa-list}]
   ["/adresa" {:post save-adresa!}]
   ["/about" {:get about-page}]])


