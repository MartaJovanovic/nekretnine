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
   {:db {:adrese/loading? true}}))

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





(defn send-adresa! [fields errors]
  (if-let [validation-errors (validate-adresa @fields)]
    (reset! errors validation-errors)
    (POST "/api/adresa"
      {:format :json
       :headers
       {"Accept" "application/transit+json"
        "x-csrf-token" (.-value (.getElementById js/document "token"))}
       :params @fields
       :handler (fn [_]
                  (rf/dispatch
                   [:adrese/add (assoc @fields :timestamp (js/Date.))])
                  (reset! fields nil)
                  (reset! errors nil))
       :error-handler (fn [e]
                        (.log js/console (str e))
                        (reset! errors (-> e :response :errors)))})))


(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (string/join error)]))






(defn adresa-form []
  (let [fields (r/atom {})
        errors (r/atom nil)]
    (fn []
      [:div
       [errors-component errors :server-error]
       [:div.field
        [:label.label {:for :ime} "Ime"]
        [errors-component errors :ime]
        [:input.input
         {:type :text
          :name :ime
          :on-change #(swap! fields
                             assoc :ime (-> % .-target .-value))
          :value (:ime @fields)}]]
       [:div.field
        [:label.label {:for :adresa} "Adresa"]
        [errors-component errors :adresa]
        [:textarea.textarea
         {:name :adresa
          :value (:adresa @fields)
          :on-change #(swap! fields
                             assoc :adresa (-> % .-target .-value))}]]
       [:input.button.is-primary
        {:type :submit
         :on-click #(send-adresa! fields errors)
         :value "Dodaj"}]])))



(defn home []
  (let [adrese (rf/subscribe [:adrese/list])]
    (rf/dispatch [:app/initialize])
    (get-adrese)
    (fn []
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       (if @(rf/subscribe [:adrese/loading?])
         [:h3 "Ucitavanje adresa..."]
         [:div
          [:div.columns>div.column
           [:h3 "Adrese"]
           [adresa-list adrese]]
          [:div.columns>div.column
           [adresa-form]]])])))


(dom/render
 [home]
 (.getElementById js/document "content"))