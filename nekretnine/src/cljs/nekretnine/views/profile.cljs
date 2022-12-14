(ns nekretnine.views.profile
  (:require
   [nekretnine.components :refer [text-input textarea-input
                                  image image-uploader]]
   [reagent.core :as r]
   [re-frame.core :as rf]))

(def profile-controllers
  [{:start (fn [_] (println "Entering Profile Page"))
    :stop (fn [_] (println "Leaving Profile Page"))}])



(rf/reg-sub
 :profile/changes
 (fn [db _]
   (get db :profile/changes)))
(rf/reg-sub
 :profile/changed?
 :<- [:profile/changes]
 (fn [changes _]
   (not (empty? changes))))
(rf/reg-sub
 :profile/field-changed?
 :<- [:profile/changes]
 (fn [changes [_ k]]
   (contains? changes k)))
(rf/reg-sub
 :profile/field
 :<- [:profile/changes]
 :<- [:auth/user]
 (fn [[changes {:keys [profile]}] [_ k default]]
   (or (get changes k) (get profile k) default)))
(rf/reg-sub
 :profile/profile
 :<- [:profile/changes]
 :<- [:auth/user]
 (fn [[changes {:keys [profile]}] _]
   (merge profile changes)))
(rf/reg-event-db
 :profile/save-change
 (fn [db [_ k v]]
   (update db :profile/changes
           (if (nil? v)
             #(dissoc % k)
             #(assoc % k v)))))

(rf/reg-event-fx
 :profile/set-profile
 (fn [_ [_ profile files]]
   (if (some some? (vals files))
     {:ajax/upload-media!
      {:url "/api/my-account/media/upload"
       :files files
       :handler
       (fn [response]
         (rf/dispatch
          [:profile/set-profile
           (merge profile
                  (select-keys response (:files-uploaded response)))]))}}
     {:ajax/post
      {:url "/api/my-account/set-profile"
       :params {:profile profile}
       :success-event [:profile/handle-set-profile profile]}})))

(rf/reg-event-db
 :profile/handle-set-profile
 (fn [db [_ profile]]
   (-> db
       (assoc-in [:auth/user :profile] profile)
       (dissoc
        :profile/media
        :profile/changes))))


(rf/reg-sub
 :profile/media
 (fn [db _]
   (get db :profile/media)))
(rf/reg-sub
 :profile/changed?
 :<- [:profile/changes]
 :<- [:profile/media]
 (fn [[changes media] _]
   (not
    (and
     (empty? changes)
     (empty? media)))))
(rf/reg-sub
 :profile/field-changed?
 :<- [:profile/changes]
 :<- [:profile/media]
 (fn [[changes media] [_ k]]
   (or
    (contains? changes k)
    (contains? media k))))
(rf/reg-sub
 :profile/field
 :<- [:profile/changes]
 :<- [:auth/user]
 :<- [:profile/media]
 (fn [[changes {:keys [profile]} media] [_ k default]]
   (or
    (when-let [file (get media k)] (js/URL.createObjectURL file))
    (get changes k)
    (get profile k)
    default)))
(rf/reg-event-db
 :profile/save-media
 (fn [db [_ k v]]
   (update db
           :profile/media
           (if (nil? v)
             #(dissoc % k)
             #(assoc % k v)))))



(defn display-name []
  (r/with-let [k :display-name
               value (rf/subscribe [:profile/field k ""])]
    [:div.field
     [:label.label {:for k} "Ime"
      (when @(rf/subscribe [:profile/field-changed? k])
        " (Promenjen)")]
     [:div.field.has-addons
      [:div.control.is-expanded
       [text-input {:value value
                    :on-save #(rf/dispatch [:profile/save-change k %])}]]
      [:div.control>button.button.is-danger
       {:disabled (not @(rf/subscribe [:profile/field-changed? k]))
        :on-click #(rf/dispatch [:profile/save-change k nil])} "Reset"]]]))

(defn bio []
  (r/with-let [k :bio
               value (rf/subscribe [:profile/field k ""])]
    [:div.field
     [:label.label {:for k} "Bio"
      (when @(rf/subscribe [:profile/field-changed? k])
        " (Promenjen)")]
     [:div.control {:style {:margin-bottom "0.5em"}}
      [textarea-input {:value value
                       :on-save #(rf/dispatch [:profile/save-change k %])}]]
     [:div.control>button.button.is-danger
      {:disabled (not @(rf/subscribe [:profile/field-changed? k]))
       :on-click #(rf/dispatch [:profile/save-change k nil])} "Reset"]]))



(defn avatar []
  (r/with-let [k :avatar
               url (rf/subscribe [:profile/field k ""])]
    [:<>
     [:h3 "Profilna"
      (when @(rf/subscribe [:profile/field-changed? k])
        " (Promenjen)")]
     [image @url 128 128]
     [:div.field.is-grouped
      [:div.control
       ^{:key @(rf/subscribe [:profile/field-changed? k])}
       [image-uploader
        #(rf/dispatch [:profile/save-media k %])
        "Izaberi profilnu "]]
      [:div.control>button.button.is-danger
       {:disabled (not @(rf/subscribe [:profile/field-changed? k]))
        :on-click #(rf/dispatch [:profile/save-media k nil])}
       "Reset profilnu "]]]))

(defn banner []
  (r/with-let [k :banner
               url (rf/subscribe [:profile/field k ""])]
    [:<>
     [:h3 "Baner"
      (when @(rf/subscribe [:profile/field-changed? k])
        " (Promenjen)")]
     [image @url 1200 400]
     [:div.field.is-grouped
      [:div.control
       [image-uploader
        #(rf/dispatch [:profile/save-media k %])
        "Izaberi baner..."]]
      [:div.control>button.button.is-danger
       {:disabled (not @(rf/subscribe [:profile/field-changed? k]))
        :on-click #(rf/dispatch [:profile/save-media k nil])}
       "Reset baner"]]]))



(defn profile [_]
  (if-let [{:keys [login created_at profile]} @(rf/subscribe [:auth/user])]
    [:div.content
     [:h1 "Moj profil"
      (str " <" login ">")]
     [:p (str "Napravljen: " (.toString created_at))]
     [display-name]
     [bio]
     [avatar]
     [banner]
     (let [disabled? (not @(rf/subscribe [:profile/changed?]))]
       [:button.button.is-primary.is-large
        {:style {:width "100%"
                 :position :sticky
                 :bottom 10
                 :visibility (if disabled?
                               :hidden
                               :visible)}
         :on-click
         #(rf/dispatch [:profile/set-profile
                        @(rf/subscribe [:profile/profile])
                        @(rf/subscribe [:profile/media])])
         :disabled disabled?}
        "Update Profile"])]
    [:div.content
     [:div {:style {:width "100%"}}
      [:progress.progress.is-dark {:max 100} "30%"]]]))
