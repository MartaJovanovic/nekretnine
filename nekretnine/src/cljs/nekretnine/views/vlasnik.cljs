(ns nekretnine.views.vlasnik
  (:require
   [re-frame.core :as rf]
   [nekretnine.adrese :as adrese]
   [reitit.frontend.easy :as rtfe]))




(rf/reg-event-fx
 ::fetch-vlasnik
 (fn [{:keys [db]} [_ login]]
   {:db (assoc db
               ::vlasnik nil
               ::loading? true)
    :ajax/get {:url (str "/api/vlasnik/" login)
               :success-event [::set-vlasnik]}}))
(rf/reg-event-db
 ::set-vlasnik
 (fn [db [_ vlasnik]]
   (if vlasnik
     (assoc db
            ::vlasnik vlasnik
            ::loading? false)
     (dissoc db ::vlasnik))))
(rf/reg-sub
 ::vlasnik
 (fn [db _]
   (get db ::vlasnik)))
(rf/reg-sub
 ::is-current-vlasnik?
 :<- [:auth/user]
 :<- [::vlasnik]
 (fn [[user vlasnik] _]
   (= (:login user) (:login vlasnik))))
(rf/reg-sub
 ::loading?
 (fn [db _]
   (::loading? db)))

(def vlasnik-controllers
  [{:parameters {:path [:user]}
    :start (fn [{{:keys [user]} :path}]
             (rf/dispatch [:adrese/load-by-vlasnik user]))}
   {:parameters {:path [:user]}
    :start (fn [{{:keys [user]} :path}]
             (rf/dispatch [::fetch-vlasnik user]))
    :stop (fn [_] (rf/dispatch [::set-vlasnik nil]))}])



(defn banner-component [url]
  [:figure.image {:style {:width "100%"
                          :height "10vw"
                          :overflow "hidden"
                          :margin-left 0
                          :margin-right 0}}
   [:img {:src url}]])
(defn title []
  (if @(rf/subscribe [::is-current-vlasnik?])
    [:div.level
     [:h2.level-left "My vlasnik Page"]
     [:a.level-right {:href (rtfe/href :guestbook.routes.app/profile)}
      "Edit Page"]]
    (let [{:keys [display-name login]} @(rf/subscribe [::vlasnik])]
      [:h2 display-name " <@" login ">'s Page"])))

(defn vlasnik [{{{:keys [user]} :path
                 {:keys [post]} :query} :parameters}]
  (let [adrese (rf/subscribe [:adrese/list])
        vlasnik (rf/subscribe [::vlasnik])]
    (fn [{{{:keys [user]} :path} :parameters}]
      (if @(rf/subscribe [::loading?])
        [:div.content
         [:div {:style {:width "100%"}}
          [:progress.progress.is-dark {:max 100} "30%"]]]
        (let [{{:keys [display-name banner bio]} :profile} @vlasnik]
          [:div.content
           [banner-component (or banner "/img/banner-default.png")]
           [title]
           (when bio
             [:p bio])
           [:div.columns.is-centered>div.column.is-two-thirds
            [:div.columns>div.column
             [:h3 "Posts by " display-name " <@" user ">"]
             (if @(rf/subscribe [:adrese/loading?])
               [adrese/adrese-list-placeholder]
               [adrese/adresa-list adrese post])]
            (when @(rf/subscribe [::is-current-vlasnik?])
              [:div.columns>div.column
               [:h4 "New Post"]
               [adrese/adresa-form]])]])))))






