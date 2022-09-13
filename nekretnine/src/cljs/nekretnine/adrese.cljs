(ns nekretnine.adrese
  (:require [clojure.string :as string]
            [nekretnine.components :refer [image image-uploader md text-input
                                           textarea-input]]
            [nekretnine.modals :as modals]
            [nekretnine.validation :refer [validate-adresa]]
            [re-frame.core :as rf]
            [reagent.core :as r]
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
   (:list
    (reduce
     (fn [{:keys [ids list] :as acc} {:keys [id] :as msg}]
       (if (contains? ids id)
         acc
         {:list (conj list msg)
          :ids (conj ids id)}))
     {:list []
      :ids #{}}
     (:adrese/list db [])))))

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

(rf/reg-event-db
 :app/hide-reply-modals
 (fn [db _]
   (update db :app/active-modals #(into
                                   {}
                                   (remove (fn [[k v]]
                                             (= :reply-modal (first k))))
                                   %))))

(rf/reg-event-fx
 :adrese/send!-called-back
 (fn [_ [_ {:keys [success errors]}]]
   (if success
     {:dispatch-n [[:form/clear-fields] [:adrese/clear-media] [:app/hide-reply-modals]]}
     {:dispatch [:form/set-server-errors errors]})))

(rf/reg-event-fx
 :form/clear
 (fn [_ _]
   {:dispatch-n [[:form/clear-fields]
                 [:adrese/clear-media]]}))

(rf/reg-event-fx
 :adrese/send!
 (fn [{:keys [db]} [_ fields media]]
   (if (not-empty media)
     {:db (dissoc db :form/server-errors)
      :ajax/upload-media!
      {:url "/api/my-account/media/upload"
       :files media
       :handler
       (fn [response] (rf/dispatch
                       [:adrese/send!
                        (update fields :adresa
                                string/replace
                                #"\!\[(.*)\]\((.+)\)"
                                (fn [[old alt url]]
                                  (str "![" alt "]("
                                       (if-some [name ((:adrese/urls db) url)]
                                         (get response name)
                                         url) ")")))]))}}
     {:db (dissoc db :form/server-errors)
      :ws/send! {:adresa [:adrese/create! fields]
                 :timeout 10000
                 :callback-event [:adrese/send!-called-back]}})
   {:db (dissoc db :form/server-errors)
    :ws/send! {:adrese [:adrese/create! fields]
               :timeout 10000
               :callback-event [:adrese/send!-called-back]}}))


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
               :adrese/filter {:poster vlasnik}
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



(rf/reg-event-db
 :adrese/save-media
 (fn [db [_ img]]
   (let [url (js/URL.createObjectURL img)
         name (keyword (str "msg-" (random-uuid)))]
     (-> db
         (update-in [:form/fields :adresa] str "![](" url ")")
         (update :adrese/media (fnil assoc {}) name img)
         (update :adrese/urls (fnil assoc {}) url name)))))

(rf/reg-sub
 :adrese/media
 (fn [db [_]]
   (:adrese/media db)))

(rf/reg-event-db
 :adrese/clear-media
 (fn [db _]
   (dissoc db :adrese/media :adrese/urls)))






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





(defn expand-post-button [{:keys [id root_id] :as m}]
  [:button.button.is-rounded.is-small.is-secondary.is-outlined.level-item
   {:on-click
    (fn [_]
      (let [{{:keys [name]} :data
             {:keys [path query]} :parameters}
            @(rf/subscribe [:router/current-route])]
        (rtfe/replace-state name path (assoc query :post id)))
      (rtfe/push-state :nekretnine.routes.app/post {:post root_id}))}
   [:i.material-icons
    "open_in_new"]])

(declare reply-modal)
(defn reply-button [{:keys [reply_count] :as m}]
  [:<>
   [reply-modal m]
   [:button.button.is-rounded.is-small.is-outlined.level-item
    {:on-click
     (fn []
       (rf/dispatch [:form/clear])
       (rf/dispatch [:app/show-modal
                     [:reply-modal (:id m)]]))
     :disabled (not= @(rf/subscribe [:auth/user-state]) :authenticated)}
    [:span.material-icons
     {:style {:font-size "inherit"}}
     "chat"]
    [:span.ml-1 reply_count]]])

(defn adresa-content [{:keys [adrese name vlasnik]
                       :as m}]
  [:<>
   (if (seq adrese)
     (doall
      (for [{:keys [adresa id] :as msg} (reverse adrese)]
        ^{:key id}
        [md :p.reply-chain-item adresa]))
     [md (:adresa m)])
   [:p " - " name
    " <"
    [:a {:href (str "/user/" vlasnik)} (str  vlasnik)]
    ">"]])

(defn post-meta [{:keys [id is_boost timestamp posted_at poster poster_avatar
                         source source_avatar] :as m}]
  (let [posted_at (or posted_at timestamp)]
    [:<>
     [:div.mb-4>time
      (if posted_at
        (.toLocaleString posted_at)
        "NULL POSTED_AT")]]))

;; (defn adresa
;;   ([m] [adresa m {}])
;;   ([{:keys [id timestamp adresa ime vlasnik avatar boosts is_boost reply_count]
;;      :or {boosts 0}
;;      :as m}
;;     {:keys [include-link?]
;;      :or {include-link? true
;;           include-bar? true}}]
;;    (let [{:keys [posted_at poster poster_avatar
;;                  source source_avatar] :as m}
;;          (if is_boost
;;            m
;;            (assoc m
;;                   :poster vlasnik
;;                   :poster_avatar avatar
;;                   :posted_at timestamp))]
;;      [:article.media
;;       [:figure.media-left
;;        [image (or avatar "/img/avatar-default.png") 128 128]]
;;       [:div.media-content
;;        [:div.content
;;         [:div.mb-4>time
;;          (.toLocaleString posted_at)]
;;         [md adresa]
;;         [:p " - " ime
;;          " <"
;;          [:a {:href (str "/user/" vlasnik)} (str "@" vlasnik)]
;;          ">"]]
;;        [:nav.level
;;         [:div.level-left

;;          (when include-link?
;;            [expand-post-button m])
;;          [reply-button m]]]]])))




(defn adresa
  ([m] [adresa m {}])
  ([{:keys [id timestamp adresa ime vlasnik avatar boosts is_boost reply_count]
     :or {boosts 0}
     :as m}
    {:keys [include-link? include-bar?]
     :or {include-link? true
          include-bar? true}}]
   [:article.media
    [:figure.media-left
     [image (or avatar "/img/avatar-default.png") 128 128]]
    [:div.media-content
     [:div.content
      [post-meta m]
      [adresa-content m]]
     (when include-bar?
       [:nav.level
        [:div.level-left
         (when include-link?
           [expand-post-button m])
         [reply-button m]]])]]))



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



(defn adresa-form-preview [parent]
  (let [{:keys [login profile]} @(rf/subscribe [:auth/user])
        msg {:adresa @(rf/subscribe [:form/field :adresa])
             :id -1
             :timestamp (js/Date.)
             :ime @(rf/subscribe [:form/field :ime])
             :vlasnik login
             :avatar (:avatar profile)}]
    [adresa-preview
     (assoc msg :adrese
            (cons msg (:adrese parent)))]))

(defn adresa-form-content []
  (let [{:keys [login profile]} @(rf/subscribe [:auth/user])]
    [:<>
     [errors-component :server-error]
     [errors-component :unauthorized "Ulogovati se pre postavljanja"]
     [:div.field
      [:label.label {:for :ime} "Ime"]
      [errors-component :ime]
      [text-input {:attrs {:name :ime}
                   :save-timeout 1000
                   :value (rf/subscribe [:form/field :ime])
                   :on-save #(rf/dispatch [:form/set-field :ime %])}]]
     [:div.field
      [:div.control
       [image-uploader
        #(rf/dispatch [:adrese/save-media %])
        "Ubaci sliku"]]]
     [:div.field
      [:label.label {:for :adresa} "Adresa"]
      [errors-component :adresa]
      [textarea-input
       {:attrs {:name :adresa}
        :save-timeout 1000
        :value (rf/subscribe [:form/field :adresa])
        :on-save #(rf/dispatch [:form/set-field :adresa %])}]]]))

(defn adresa-form-content-reply []
  (let [{:keys [login profile]} @(rf/subscribe [:auth/user])]
    [:<>
     [errors-component :server-error]
     [errors-component :unauthorized "Ulogovati se pre postavljanja"]
     [:div.field
      [:label.label {:for :ime} "Ime"]
      [errors-component :ime]
      [text-input {:attrs {:name :ime}
                   :save-timeout 1000
                   :value (rf/subscribe [:form/field :ime])
                   :on-save #(rf/dispatch [:form/set-field :ime %])}]]
     [:div.field
      [:div.control
       [image-uploader
        #(rf/dispatch [:adrese/save-media %])
        "Ubaci sliku"]]]
     [:div.field
      [:label.label {:for :adresa} "Odgovor"]
      [errors-component :adresa]
      [textarea-input
       {:attrs {:name :adresa}
        :save-timeout 1000
        :value (rf/subscribe [:form/field :adresa])
        :on-save #(rf/dispatch [:form/set-field :adresa %])}]]]))




(defn reply-modal [parent]
  [modals/modal-card
   {:on-close #(rf/dispatch [:form/clear])
    :id [:reply-modal (:id parent)]}
   (str "Odgovori na objavu  " (:vlasnik parent))
   [:<>
    [adresa-form-preview parent]
    [adresa-form-content-reply]]
   [:input.button.is-primary.is-fullwidth
    {:type :submit
     :disabled @(rf/subscribe [:form/validation-errors?])
     :on-click #(rf/dispatch [:adrese/send!
                              (assoc
                               @(rf/subscribe [:form/fields])
                               :parent (:id parent))
                              @(rf/subscribe [:adrese/media])])
     :value (str "Odgovori  " (:vlasnik parent))}]])

;; (defn reply-modal [parent]
;;   [modals/modal-card
;;    [:reply-modal (:id parent)]
;;    (str "Reply to post by user: " (:vlasnik parent))
;;    [:<>
;;     [adresa-form-preview parent]
;;     [adresa-form-content]]
;;    [:input.button.is-primary.is-fullwidth
;;     {:type :submit
;;      :disabled @(rf/subscribe [:form/validation-errors?])
;;      :on-click #(rf/dispatch [:adrese/send!
;;                               (assoc
;;                                @(rf/subscribe [:form/fields])
;;                                :parent (:id parent))
;;                               @(rf/subscribe [:adrese/media])])
;;      :value (str "Reply to " (:vlasnik parent))}]])


(defn adresa-form []
  [:div.card
   [:div.card-header>p.card-header-title
    "Objavi nesto"]
   [:div.card-content
    [adresa-form-preview {}]
    [adresa-form-content]
    [:input.button.is-primary.is-fullwidth
     {:type :submit
      :disabled @(rf/subscribe [:form/validation-errors?])
      :on-click
      (fn []
        (rf/dispatch [:adrese/send!
                      @(rf/subscribe [:form/fields])
                      @(rf/subscribe [:adrese/media])])
        (rf/dispatch [:adrese/load]))
      :value "Dodaj"}]]])


;; (defn adresa-form []
;;   [:div.card
;;    [:div.card-header>p.card-header-title
;;     "Objavi"]
;;    (let [{:keys [login profile]} @(rf/subscribe [:auth/user])]
;;      [:div.card-content
;;       [adresa-preview {:adresa @(rf/subscribe [:form/field :adresa])
;;                        :id -1
;;                        :timestamp (js/Date.)
;;                        :ime @(rf/subscribe [:form/field :ime])
;;                        :vlasnik login
;;                        :avatar (:avatar profile)}]
;;       [errors-component :server-error]
;;       [errors-component :unauthorized "Please log in before posting."]
;;       [:div.field
;;        [:label.label {:for :ime} "Ime"]
;;        [errors-component :ime]
;;        [text-input {:attrs {:name :ime}
;;                     :save-timeout 1000
;;                     :value (rf/subscribe [:form/field :ime])
;;                     :on-save #(rf/dispatch [:form/set-field :ime %])}]]
;;       [:div.field
;;        [:label.label {:for :adresa} "Adresa"]
;;        [errors-component :adresa]
;;        [textarea-input
;;         {:attrs {:name :adresa}
;;          :save-timeout 1000
;;          :value (rf/subscribe [:form/field :adresa])
;;          :on-save #(rf/dispatch [:form/set-field :adresa %])}]]
;;       [:div.field
;;        [:div.control
;;         [image-uploader
;;          #(rf/dispatch [:adrese/save-media %])
;;          "Ubaci sliku"]]]
;;       [:input.button.is-primary.is-fullwidth
;;        {:type :submit
;;         :disabled @(rf/subscribe [:form/validation-errors?])
;;         :on-click #(rf/dispatch [:adrese/send!
;;                                  @(rf/subscribe [:form/fields])
;;                                  @(rf/subscribe [:adrese/media])])
;;         :value "Dodaj"}]])])


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








