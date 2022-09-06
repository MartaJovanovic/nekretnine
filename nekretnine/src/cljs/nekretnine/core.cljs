(ns nekretnine.core
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [ajax.core :refer [GET POST]]
            [clojure.string :as string]
            [nekretnine.validation :refer [validate-adresa]]))



(defn adresa-list [adrese]
  (println adrese)
  [:ul.adrese
   (for [{:keys [timestamp adresa ime]} @adrese]
     ^{:key timestamp}
     [:li
      [:time (.toLocaleString timestamp)]
      [:p adresa]
      [:p " - " ime]])])



(defn get-adrese [adrese]
  (GET "/adrese"
    {:headers {"Accept" "application/transit+json"}
     :handler #(reset! adrese (:adrese %))}))







(defn send-adresa! [fields errors adrese]
  (POST "/adresa"
    {:format :json
     :headers
     {"Accept" "application/transit+json"
      "x-csrf-token" (.-value (.getElementById js/document "token"))}
     :params @fields
     :handler (fn [_]
                (swap! adrese conj (assoc @fields
                                          :timestamp (js/Date.)))
                (reset! fields nil)
                (reset! errors nil))
     :error-handler (fn [e]
                      (.log js/console (str e))
                      (reset! errors (-> e :response :errors)))}))




(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (string/join error)]))





(defn adresa-form [adrese]
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
         :on-click #(send-adresa! fields errors adrese)
         :value "Dodaj"}]])))



(defn home []
  (let [adrese (r/atom nil)]
    (get-adrese adrese)
    (fn []
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       [:div.columns>div.column
        [:h3 "Adrese"]
        [adresa-list adrese]]
       [:div.columns>div.column
        [adresa-form adrese]]])))


(dom/render
 [home]
 (.getElementById js/document "content"))
