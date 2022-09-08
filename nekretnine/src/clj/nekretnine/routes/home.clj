(ns nekretnine.routes.home
  (:require
   [nekretnine.layout :as layout]
   [nekretnine.db.core :as db]
   [nekretnine.adrese :as adr]
   [clojure.java.io :as io]
   [nekretnine.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [nekretnine.validation :refer [validate-adresa]]))


(defn adresa-list [_]
  (response/ok (adr/adresa-list)))


(defn save-adresa! [{:keys [params]}]
  (try
    (adr/save-adresa! params)
    (response/ok {:status :ok})
    (catch Exception e
      (let [{id :nekretnine/error-id
             errors :errors} (ex-data e)]
        (case id
          :validation
          (response/bad-request {:errors errors})
;;else
          (response/internal-server-error
           {:errors {:server-error ["Neuspelo cuvanje"]}}))))))


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


