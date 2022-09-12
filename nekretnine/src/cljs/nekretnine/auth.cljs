(ns nekretnine.auth
  (:require
   [clojure.string :as string]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [nekretnine.modals :as m]
   [ajax.core :refer [POST]]
   [reitit.frontend.easy :as rtfe]))

(rf/reg-event-fx
 :session/load
 (fn [{:keys [db]} _]
   {:db (assoc db :session/loading? true)
    :ajax/get {:url "/api/session"
               :success-path [:session]
               :success-event [:session/set]}}))
(rf/reg-event-db
 :session/set
 (fn [db [_ {:keys [identity]}]]
   (assoc db
          :auth/user identity
          :session/loading? false)))
(rf/reg-sub
 :session/loading?
 (fn [db _]
   (:session/loading? db)))
(rf/reg-event-db
 :auth/handle-login
 (fn [db [_ {:keys [identity]}]]
   (assoc db :auth/user identity)))
(rf/reg-event-db
 :auth/handle-logout
 (fn [db _]
   (dissoc db :auth/user)))
(rf/reg-sub
 :auth/user
 (fn [db _]
   (:auth/user db)))
(rf/reg-sub
 :auth/user-state
 :<- [:auth/user]
 :<- [:session/loading?]
 (fn [[user loading?]]
   (cond
     (true? loading?) :loading
     user :authenticated
     :else :anonymous)))


(defn login-button []
  (r/with-let
    [fields (r/atom {})
     error (r/atom nil)
     do-login
     (fn [_]
       (reset! error nil)
       (POST "/api/login"
         {:headers {"Accept" "application/transit+json"}
          :params @fields
          :handler (fn [response]
                     (reset! fields {})
                     (rf/dispatch [:auth/handle-login response])
                     (rf/dispatch [:app/hide-modal :user/login]))
          :error-handler (fn [error-response]
                           (reset! error
                                   (or
                                    (:message (:response error-response))
                                    (:status-text error-response)
                                    "Unknown Error")))}))]
    [m/modal-button :user/login
;; Title
     "Log In"
;; Body
     [:div
      (when-not (string/blank? @error)
        [:div.notification.is-danger
         @error])
      [:div.field
       [:div.label "Login"]
       [:div.control
        [:input.input
         {:type "text"
          :value (:login @fields)
          :on-change #(swap! fields assoc :login (.. % -target -value))}]]]
      [:div.field
       [:div.label "lozinka"]
       [:div.control
        [:input.input
         {:type "lozinka"
          :value (:lozinka @fields)
          :on-change #(swap! fields assoc :lozinka (.. % -target -value))
;; Submit login form when `Enter` key is pressed
          :on-key-down #(when (= (.-keyCode %) 13)
                          (do-login))}]]]]
;; Footer
     [:button.button.is-primary.is-fullwidth
      {:on-click do-login
       :disabled (or (string/blank? (:login @fields))
                     (string/blank? (:lozinka @fields)))}
      "Log In"]]))


(defn register-button []
  (r/with-let
    [fields (r/atom {})
     error (r/atom nil)
     do-register
     (fn [_]
       (reset! error nil)
       (POST "/api/register"
         {:headers {"Accept" "application/transit+json"}
          :params @fields
          :handler (fn [response]
                     (reset! fields {})
                     (rf/dispatch [:app/hide-modal :user/register])
                     (rf/dispatch [:app/show-modal :user/login]))
          :error-handler (fn [error-response]
                           (reset! error
                                   (or
                                    (:message (:response error-response))
                                    (:status-text error-response)
                                    "Unknown Error")))}))]
    [m/modal-button :user/register
;; Title
     "Napravi novi nalog"
;; Body
     [:div
      (when-not (string/blank? @error)
        [:div.notification.is-danger
         @error])
      [:div.field
       [:div.label "Login"]
       [:div.control
        [:input.input
         {:type "text"
          :value (:login @fields)
          :on-change #(swap! fields assoc :login (.. % -target -value))}]]]
      [:div.field
       [:div.label "lozinka"]
       [:div.control
        [:input.input
         {:type "lozinka"
          :value (:lozinka @fields)
          :on-change #(swap! fields assoc :lozinka (.. % -target -value))}]]]
      [:div.field
       [:div.label "Potvrdi lozinku"]
       [:div.control
        [:input.input
         {:type "lozinka"
          :value (:confirm @fields)
          :on-change #(swap! fields assoc :confirm (.. % -target -value))
 ;; Submit login form when `Enter` key is pressed
          :on-key-down #(when (= (.-keyCode %) 13)
                          (do-register))}]]]]
;; Footer
     [:button.button.is-primary.is-fullwidth
      {:on-click do-register
       :disabled (or (string/blank? (:login @fields))
                     (string/blank? (:lozinka @fields))
                     (string/blank? (:confirm @fields)))}
      "Napravi novi nalog"]]))

(defn logout-button []
  [:button.button
   {:on-click #(POST "/api/logout"
                 {:handler (fn [_] (rf/dispatch [:auth/handle-logout]))})}
   "Log Out"])


(defn nameplate [{:keys [login]}]
  [:a.button.is-primary
   {:href (rtfe/href :nekretnine.routes.app/profile)}
   login])
