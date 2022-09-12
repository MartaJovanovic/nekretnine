(ns nekretnine.adrese
  (:require
   [clojure.string :as string]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [nekretnine.validation :refer [validate-adresa]]
   [nekretnine.components :refer [text-input textarea-input image md]]
   [reagent.dom :as dom]
   [reitit.frontend.easy :as rtfe]))


(rf/reg-event-fx
 :adrese/load
 (fn [{:keys [db]} _]
   {:db (assoc db
               :adrese/loading? true
               :adrese/list nil
               :adrese/filter nil)
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


(rf/reg-event-fx
 :adrese/load-by-vlasnik
 (fn [{:keys [db]} [_ vlasnik]]
   {:db (assoc db
               :adrese/loading? true
               :adrese/filter {:vlasnik vlasnik}
               :adrese/list nil)
    :ajax/get {:url (str "/api/adrese/by/" vlasnik)
               :success-path [:adrese]
               :success-event [:adrese/set]}}))



(defn add-adresa? [filter-map adr]
  (every?
   (fn [[k matcher]]
     (let [v (get adr k)]
       (cond
         (set? matcher)
         (matcher v)
         (fn? matcher)
         (matcher v)
         :else
         (= matcher v))))
   filter-map))

(rf/reg-event-db
 :adrese/add
 (fn [db [_ adrese]]
   (if (add-adresa? (:adrese/filter db) adrese)
     (update db :adrese/list conj adrese)
     db)))

(defn adrese-list-placeholder []
  [:ul.adrese
   [:li
    [:p "Ucitavanje adresa..."]
    [:div {:style {:width "10em"}}
     [:progress.progress.is-dark {:max 100} "30%"]]]])




(defn errors-component [id & [message]]
  (when-let [error @(rf/subscribe [:form/error id])]
    [:div.notification.is-danger (if message
                                   message
                                   (string/join error))]))



(defn reload-adrese-button []
  (let [loading? (rf/subscribe [:adrese/loading?])]
    [:button.button.is-info.is-fullwidth
     {:on-click #(rf/dispatch [:adrese/load])
      :disabled @loading?}
     (if @loading?
       "Ucitavanje adresa"
       "Refresuj")]))


(defn adresa
  ([m] [adresa m {}])
  ([{:keys [id timestamp adresa ime vlasnik avatar] :as m}
    {:keys [include-link?]
     :or {include-link? true}}]
   [:article.media
    [:figure.media-left
     [image (or avatar "/img/avatar-default.png") 128 128]]
    [:div.media-content>div.content
     [:time (.toLocaleString timestamp)]
     [md adresa]
     (when include-link?
       [:p>a {:on-click
              (fn [_]
                (let [{{:keys [ime]} :data
                       {:keys [path query]} :parameters}
                      @(rf/subscribe [:router/current-route])]
                  (rtfe/replace-state ime path (assoc query :post id)))
                (rtfe/push-state :nekretnine.routes.app/post {:post id}))}
        "View Post"])
     [:p " - " ime
      " <"

      [:a {:href (str "/user/" vlasnik)} (str vlasnik)]
      ">"]]]))


(defn adresa-preview [m]
  (r/with-let [expanded (r/atom false)]
    [:<>
     [:button.button.is-secondary.is-fullwidth
      {:on-click #(swap! expanded not)}
      (if @expanded
        "Hide Preview"
        "Show Preview")]
     (when @expanded
       [:ul.adrese
        {:style
         {:margin-left 0}}
        [:li
         [adresa m
          {:include-link? false}]]])]))



(defn adresa-form []
  [:div.card
   [:div.card-header>p.card-header-title
    "Objavi"]
   (let [{:keys [login profile]} @(rf/subscribe [:auth/user])]
     [:div.card-content
      [adresa-preview {:adresa @(rf/subscribe [:form/field :adresa])
                       :id -1
                       :timestamp (js/Date.)
                       :ime @(rf/subscribe [:form/field :ime])
                       :author login
                       :avatar (:avatar profile)}]
      [errors-component :server-error]
      [errors-component :unauthorized "Please log in before posting."]
      [:div.field
       [:label.label {:for :name} "Name"]
       [errors-component :ime]
       [text-input {:attrs {:name :ime}
                    :save-timeout 1000
                    :value (rf/subscribe [:form/field :ime])
                    :on-save #(rf/dispatch [:form/set-field :ime %])}]]
      [:div.field
       [:label.label {:for :adresa} "Adresa"]
       [errors-component :adresa]
       [textarea-input
        {:attrs {:name :adresa}
         :save-timeout 1000
         :value (rf/subscribe [:form/field :adresa])
         :on-save #(rf/dispatch [:form/set-field :adresa %])}]]
      [:input.button.is-primary.is-fullwidth
       {:type :submit
        :disabled @(rf/subscribe [:form/validation-errors?])
        :on-click #(rf/dispatch [:adrese/send!
                                 @(rf/subscribe [:form/fields])])
        :value "Dodaj"}]])])


(defn adr-li [m adr-id]
  (r/create-class
   {:component-did-mount
    (fn [this]
      (when (= adr-id (:id m))
        (.scrollIntoView (dom/dom-node this))))
    :reagent-render
    (fn [_]
      [:li
       [adresa m]])}))

(defn adresa-list
  ([adrese]
   [adresa-list adrese nil])
  ([adrese adr-id]
   [:ul.adrese
    (for [m @adrese]
      ^{:key (:timestamp m)}
      [adr-li m adr-id])]))