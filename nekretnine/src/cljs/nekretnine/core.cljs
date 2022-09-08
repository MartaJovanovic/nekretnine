(ns nekretnine.core
  (:require  [reagent.core :as r]
             [reagent.dom :as dom]
             [re-frame.core :as rf]
             [ajax.core :refer [GET POST]]
             [clojure.string :as string]
             [nekretnine.validation :refer [validate-adresa]]))



(rf/reg-event-fx
 :app/initialize
 (fn [_ _]
   {:db {:adrese/loading? true}
    :dispatch [:adrese/load]}))

(rf/reg-event-fx
 :adrese/load
 (fn [{:keys [db]} _]
   (GET "/api/adrese"
     {:headers {"Accept" "application/transit+json"}
      :handler #(rf/dispatch [:adrese/set (:adrese %)])})
   {:db (assoc db :adrese/loading? true)}))



(rf/reg-sub
 :adrese/loading?
 (fn [db _]
   (:adrese/loading? db)))

(rf/reg-sub
 :adrese/list
 (fn [db _]
   (:adrese/list db [])))

(rf/reg-event-db
 :adrese/set
 (fn [db [_ adrese]]
   (-> db
       (assoc :adrese/loading? false
              :adrese/list adrese))))

(rf/reg-event-db
 :adrese/add
 (fn [db [_ adrese]]
   (update db :adrese/list conj adrese)))

(rf/reg-event-db
 :form/set-field
 [(rf/path :form/fields)]
 (fn [fields [_ id value]]
   (assoc fields id value)))

(rf/reg-event-db
 :form/clear-fields
 [(rf/path :form/fields)]
 (fn [_ _]
   {}))

(rf/reg-sub
 :form/fields
 (fn [db _]
   (:form/fields db)))

(rf/reg-sub
 :form/field
 :<- [:form/fields]
 (fn [fields [_ id]]
   (get fields id)))

(rf/reg-event-db
 :form/set-server-errors
 [(rf/path :form/server-errors)]
 (fn [_ [_ errors]]
   errors))

(rf/reg-sub
 :form/server-errors
 (fn [db _]
   (:form/server-errors db)))

;;Validation errors are reactively computed

(rf/reg-sub
 :form/validation-errors
 :<- [:form/fields]
 (fn [fields _]
   (validate-adresa fields)))

(rf/reg-sub
 :form/validation-errors?
 :<- [:form/validation-errors]
 (fn [errors _]
   (not (empty? errors))))

(rf/reg-sub
 :form/errors
 :<- [:form/validation-errors]
 :<- [:form/server-errors]
 (fn [[validation server] _]
   (merge validation server)))

(rf/reg-sub
 :form/error
 :<- [:form/errors]
 (fn [errors [_ id]]
   (get errors id)))

(rf/reg-event-fx
 :adrese/send!
 (fn [{:keys [db]} [_ fields]]
   (POST "/api/adresa"
     {:format :json
      :headers
      {"Accept" "application/transit+json"
       "x-csrf-token" (.-value (.getElementById js/document "token"))}
      :params fields
      :handler #(rf/dispatch
                 [:adrese/add
                  (-> fields
                      (assoc :timestamp (js/Date.)))])
      :error-handler #(rf/dispatch
                       [:form/set-server-errors
                        (get-in % [:response :errors])])})
   {:db (dissoc db :form/server-errors)}))








(defn adresa-list [adrese]
  (println adrese)
  [:ul.adrese
   (for [{:keys [timestamp adresa ime]} @adrese]
     ^{:key timestamp}
     [:li
      [:time (.toLocaleString timestamp)]
      [:p adresa]
      [:p " - " ime]])])



(defn get-adrese []
  (GET "/api/adrese"
    {:headers {"Accept" "application/transit+json"}
     :handler #(rf/dispatch [:adrese/set (:adrese %)])}))



(defn errors-component [id]
  (when-let [error @(rf/subscribe [:form/error id])]
    [:div.notification.is-danger (string/join error)]))





(defn adresa-form []
  [:div
   [errors-component  :server-error]
   [:div.field
    [:label.label {:for :ime} "Ime"]
    [errors-component :ime]
    [:input.input
     {:type :text
      :name :ime
      :on-change #(rf/dispatch
                   [:form/set-field
                    :ime
                    (.. % -target -value)])
      :value @(rf/subscribe [:form/field :ime])}]]
   [:div.field
    [:label.label {:for :adresa} "Adresa"]
    [errors-component :adresa]
    [:textarea.textarea
     {:name :adresa
      :value  @(rf/subscribe [:form/field :adresa])
      :on-change  #(rf/dispatch
                    [:form/set-field
                     :adresa
                     (.. % -target -value)])}]]
   [:input.button.is-primary
    {:type :submit
     :disabled @(rf/subscribe [:form/validation-errors?])
     :on-click  #(rf/dispatch [:adrese/send!
                               @(rf/subscribe [:form/fields])])
     :value "Dodaj"}]])



(defn reload-adrese-button []
  (let [loading? (rf/subscribe [:adrese/loading?])]
    [:button.button.is-info.is-fullwidth
     {:on-click #(rf/dispatch [:adrese/load])
      :disabled @loading?}
     (if @loading?
       "Ucitavanje adresa"
       "Refresuj")]))




(defn home []
  (let [adrese (rf/subscribe [:adrese/list])]
    (fn []
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       [:div.columns>div.column
        [:h3 "Adrese"]
        [adresa-list adrese]]
       [:div.columns>div.column
        [reload-adrese-button]]
       [:div.columns>div.column
        [adresa-form]]])))

(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (.log js/console "Mounting Components...")
  (dom/render [#'home] (.getElementById js/document "content"))
  (.log js/console "Components Mounted!"))


(defn init! []
  (.log js/console "Initializing App...")
  (rf/dispatch [:app/initialize])
  (get-adrese)
  (mount-components))

(.log js/console "nekretnine.core evaluated!")

(dom/render
 [home]
 (.getElementById js/document "content"))