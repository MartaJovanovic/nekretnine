(ns nekretnine.routes.app
  (:require  [spec-tools.data-spec :as ds]
             #?@(:clj [[nekretnine.layout :as layout]
                       [nekretnine.middleware :as middleware]]
                 :cljs [[nekretnine.views.home :as home]
                        [nekretnine.views.vlasnik :as vlasnik]
                        [nekretnine.views.profile :as profile]
                        [nekretnine.views.post :as post]])))
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
        {:parameters {:query {(ds/opt :post) pos-int?}}
         :controllers home/home-controllers
         :view #'home/home}))]

   ["/user/:user"
    (merge
     {:name ::vlasnik}
     #?(:cljs {:parameters {:query {(ds/opt :post) pos-int?}
                            :path {:user string?}}
               :controllers vlasnik/vlasnik-controllers
               :view #'vlasnik/vlasnik}))]
   ["/my-account/edit-profile"
    (merge
     {:name ::profile}
     #?(:cljs
        {:controllers profile/profile-controllers
         :view #'profile/profile}))]
   ["/post/:post"
    (merge
     {:name ::post}
     #?(:cljs {:parameters {:path {:post pos-int?}}
               :controllers post/post-controllers
               :view #'post/post-page}))]])

