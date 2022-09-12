(ns nekretnine.views.vlasnik
  (:require
   [re-frame.core :as rf]
   [nekretnine.adrese :as adrese]))

(defn vlasnik [{{{:keys [user]} :path} :parameters}]
  (let [adrese (rf/subscribe [:adrese/list])]
    (fn [{{{:keys [user]} :path} :parameters}]
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       [:div.columns>div.column
        [:h3 "Objavio/la  " user]
        (if @(rf/subscribe [:adrese/loading?])
          [adrese/adrese-list-placeholder]
          [adrese/adresa-list adrese])]])))

(def vlasnik-controllers
  [{:parameters {:path [:user]}
    :start (fn [{{:keys [user]} :path}]
             (rf/dispatch [:adrese/load-by-vlasnik user]))}])
