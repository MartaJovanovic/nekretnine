(ns nekretnine.routes.app
  (:require
   #?@(:clj [[nekretnine.layout :as layout]
             [nekretnine.middleware :as middleware]]
       :cljs [[nekretnine.views.home :as home]
              [nekretnine.views.vlasnik :as vlasnik]])))
#?(:clj
   (defn home-page [request]
     (layout/render
      request
      "home.html")))

(defn app-routes []
  [""
   #?(:clj {:middleware [middleware/wrap-csrf]
            :get home-page})
   ["/"
    (merge
     {:name ::home}
     #?(:cljs
        {:view #'home/home}))]
   ["/user/:user"
    (merge
     {:name ::vlasnik}
     #?(:cljs {:view #'vlasnik/vlasnik}))]])

