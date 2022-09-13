(ns nekretnine.views.home
  (:require
   [re-frame.core :as rf]
   [nekretnine.adrese :as adr]
   [nekretnine.auth :as auth]))

(defn home [{{{post :post} :query} :parameters}]
  (let [adrese (rf/subscribe [:adrese/list])]
    (fn []
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       [:div.columns>div.column
        [:h3 "Adrese"]
        (if @(rf/subscribe [:adrese/loading?])
          [adr/adrese-list-placeholder]
          [adr/adresa-list adrese post])]
       [:div.columns>div.column
        [adr/reload-adrese-button]]
       [:div.columns>div.column
        (case @(rf/subscribe [:auth/user-state])
          :loading
          [:div {:style {:width "5em"}}
           [:progress.progress.is-dark.is-small {:max 100} "30%"]]
          :authenticated
          [adr/adresa-form]
          :anonymous
          [:div.notification.is-clearfix
           [:span "Log in da bi postavio oglas!\n"]
           [:div.buttons.is-pulled-right
            [auth/login-button]
            [auth/register-button]]])]])))

(def home-controllers
  [{:start (fn [_] (rf/dispatch [:adrese/load]))}])