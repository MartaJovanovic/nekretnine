(ns nekretnine.routes.app
  (:require
   #?@(:clj [[nekretnine.layout :as layout]
             [nekretnine.middleware :as middleware]]
       :cljs [[nekretnine.views.home :as home]
              [nekretnine.views.vlasnik :as vlasnik]
              [nekretnine.views.profile :as profile]])))
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
        {:controllers home/home-controllers
         :view #'home/home}))]
   ["/user/:user"
    (merge
     {:name ::vlasnik}
     #?(:cljs  {:controllers vlasnik/vlasnik-controllers
                :view #'vlasnik/vlasnik}))]
   ["/my-account/edit-profile"
    (merge
     {:name ::profile}
     #?(:cljs
        {:controllers profile/profile-controllers
         :view #'profile/profile}))]])

