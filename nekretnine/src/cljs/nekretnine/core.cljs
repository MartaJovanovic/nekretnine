(ns nekretnine.core
  (:require  [reagent.core :as r]
             [reagent.dom :as dom]
             [re-frame.core :as rf]
             [ajax.core :refer [GET POST]]
             [clojure.string :as string]
             [nekretnine.validation :refer [validate-adresa]]
             [nekretnine.websockets :as ws]
             [mount.core :as mount]))


(rf/reg-fx
 :ajax/get
 (fn [{:keys [url success-event error-event success-path]}]
   (GET url
     (cond-> {:headers {"Accept" "application/transit+json"}}
       success-event (assoc :handler
                            #(rf/dispatch
                              (conj success-event
                                    (if success-path
                                      (get-in % success-path)
                                      %))))
       error-event (assoc :error-handler
                          #(rf/dispatch
                            (conj error-event %)))))))

(defn text-input [{val :value
                   attrs :attrs
                   :keys [on-save]}]
  (let [draft (r/atom nil)
        value (r/track #(or @draft @val ""))]
    (fn []
      [:input.input
       (merge attrs
              {:type :text
               :on-focus #(reset! draft (or @val ""))
               :on-blur (fn []
                          (on-save (or @draft ""))
                          (reset! draft nil))
               :on-change #(reset! draft (.. % -target -value))
               :value @value})])))

(defn textarea-input [{val :value
                       attrs :attrs
                       :keys [on-save]}]
  (let [draft (r/atom nil)
        value (r/track #(or @draft @val ""))]
    (fn []
      [:textarea.textarea
       (merge attrs
              {:on-focus #(reset! draft (or @val ""))
               :on-blur (fn []
                          (on-save (or @draft ""))
                          (reset! draft nil))
               :on-change #(reset! draft (.. % -target -value))
               :value @value})])))



(rf/reg-event-fx
 :app/initialize
 (fn [_ _]
   {:db {:adrese/loading? true
         :session/loading? true}
    :dispatch-n [[:session/load] [:adrese/load]]}))

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




(rf/reg-event-fx
 :adrese/load
 (fn [{:keys [db]} _]
   {:db (assoc db :adrese/loading? true)
    :ajax/get {:url "/api/adrese"
               :success-path [:adrese]
               :success-event [:adrese/set]}}))

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
 :adrese/send!-called-back
 (fn [_ [_ {:keys [success errors]}]]
   (if success
     {:dispatch [:form/clear-fields]}
     {:dispatch [:form/set-server-errors errors]})))


(rf/reg-event-fx
 :adrese/send!
 (fn [{:keys [db]} [_ fields]]
   {:db (dissoc db :form/server-errors)
    :ws/send! {:adrese [:adrese/create! fields]
               :timeout 10000
               :callback-event [:adrese/send!-called-back]}}))


(rf/reg-event-db
 :app/show-modal
 (fn [db [_ modal-id]]
   (assoc-in db [:app/active-modals modal-id] true)))

(rf/reg-event-db
 :app/hide-modal
 (fn [db [_ modal-id]]
   (update db :app/active-modals dissoc modal-id)))

(rf/reg-sub
 :app/active-modals
 (fn [db _]
   (:app/active-modals db {})))

(rf/reg-sub
 :app/modal-showing?
 :<- [:app/active-modals]
 (fn [modals [_ modal-id]]
   (get modals modal-id false)))

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



(defn modal-card [id title body footer]
  [:div.modal
   {:class (when @(rf/subscribe [:app/modal-showing? id]) "is-active")}
   [:div.modal-background
    {:on-click #(rf/dispatch [:app/hide-modal id])}]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title title]
     [:button.delete
      {:on-click #(rf/dispatch [:app/hide-modal id])}]]
    [:section.modal-card-body
     body]
    [:footer.modal-card-foot
     footer]]])

(defn modal-button [id title body footer]
  [:div
   [:button.button.is-primary
    {:on-click #(rf/dispatch [:app/show-modal id])}
    title]
   [modal-card id title body footer]])





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
    [modal-button :user/login
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
    [modal-button :user/register
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
                 {:handler (fn [_]
                             (rf/dispatch [:auth/handle-logout]))})}
   "Log Out"])
(defn nameplate [{:keys [login]}]
  [:button.button.is-primary
   login])



(defn handle-response! [response]
  (if-let [errors (:errors response)]
    (rf/dispatch [:form/set-server-errors errors])
    (do
      (rf/dispatch [:adrese/add response])
      (rf/dispatch [:form/clear-fields response]))))



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
    [text-input {:attrs {:name :ime}
                 :value (rf/subscribe [:form/field :ime])
                 :on-save #(rf/dispatch [:form/set-field :ime %])}]]
   [:div.field
    [:label.label {:for :adresa} "Adresa"]
    [errors-component :adresa]
    [textarea-input
     {:attrs {:name :adresa}
      :value (rf/subscribe [:form/field :adresa])
      :on-save #(rf/dispatch [:form/set-field :adresa %])}]]
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

(defn navbar []
  (let [burger-active (r/atom false)]
    (fn []
      [:nav.navbar.is-info
       [:div.container
        [:div.navbar-brand
         [:a.navbar-item
          {:href "/"
           :style {:font-weight "bold"}}
          "nekretnine"]
         [:span.navbar-burger.burger
          {:data-target "nav-menu"
           :on-click #(swap! burger-active not)
           :class (when @burger-active "is-active")}
          [:span]
          [:span]
          [:span]]]
        [:div#nav-menu.navbar-menu
         {:class (when @burger-active "is-active")}
         [:div.navbar-start
          [:a.navbar-item
           {:href "/"}
           "Home"]]
         [:div.navbar-end
          [:div.navbar-item
           (case @(rf/subscribe [:auth/user-state])
             :loading
             [:div {:style {:width "5em"}}
              [:progress.progress.is-dark.is-small {:max 100} "30%"]]
             :authenticated
             [:div.buttons
              [nameplate @(rf/subscribe [:auth/user])]
              [logout-button]]
             :anonymous
             [:div.buttons
              [login-button]
              [register-button]])]]]]])))


(defn app []
  [:div.app
   [navbar]
   [:section.section
    [:div.container
     [home]]]])







(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (.log js/console "Mounting Components...")
  (dom/render [#'app] (.getElementById js/document "content"))
  (.log js/console "Components Mounted!"))


(defn init! []
  (.log js/console "Initializing App...")
  (mount/start)
  (rf/dispatch [:app/initialize])
  (mount-components))


(.log js/console "nekretnine.core evaluated!")

(dom/render
 [home]
 (.getElementById js/document "content"))