(ns nekretnine.routes.services
  (:require [nekretnine.adrese :as adr]
            [nekretnine.auth :as auth]
            [nekretnine.auth.ring :refer [wrap-authorized get-roles-from-match]]
            [clojure.tools.logging :as log]
            [nekretnine.middleware.formats :as formats]
            [reitit.coercion.spec :as spec-coercion]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [ring.util.http-response :as response]
            [spec-tools.data-spec :as ds]
            [nekretnine.vlasnik :as vlasnik]
            [clojure.java.io :as io]
            [nekretnine.db.core :as db]
            [nekretnine.media :as media]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]))



(defn service-routes []
  ["/api"
   {:middleware [;; query-params & form-params
                 parameters/parameters-middleware
;; content-negotiation
                 muuntaja/format-negotiate-middleware
;; encoding response body
                 muuntaja/format-response-middleware
;; exception handling
                 exception/exception-middleware
;; decoding request body
                 muuntaja/format-request-middleware
;; coercing response bodys
                 coercion/coerce-response-middleware
;; coercing request parameters
                 coercion/coerce-request-middleware
;; multipart params
                 multipart/multipart-middleware
                 (fn [handler]
                   (wrap-authorized
                    handler
                    (fn handle-unauthorized [req]
                      (let [route-roles (get-roles-from-match req)]
                        (log/debug
                         "Roles for route: "
                         (:uri req)
                         route-roles) (log/debug "User is unauthorized!"
                                                 (-> req
                                                     :session
                                                     :identity
                                                     :roles))
                        (response/forbidden
                         {:message
                          (str "User must have one of the following roles: "
                               route-roles)})))))]

    :muuntaja formats/instance
    :coercion spec-coercion/coercion
    :swagger {:id ::api}}
   ["" {:no-doc true
        ::auth/roles (auth/roles :swagger/swagger)}
    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]
    ["/swagger-ui*"
     {:get (swagger-ui/create-swagger-ui-handler
            {:url "/api/swagger.json"})}]]
   ["/adrese"
    {::auth/roles (auth/roles :adrese/list)}
    ["" {:get
         {:responses
          {200
           {:body ;; Data Spec for response body
            {:adrese
             [{:id pos-int?
               :ime string?
               :adresa string?
               :timestamp inst?
               :vlasnik (ds/maybe string?)
               :avatar (ds/maybe string?)}]}}}
          :handler
          (fn [_]
            (Thread/sleep 500)
            (response/ok (adr/adresa-list)))}}]
    ["/by/:vlasnik"
     {:get
      {:parameters {:path {:vlasnik string?}}
       :responses
       {200
        {:body ;; Data Spec for response body
         {:adrese
          [{:id pos-int?
            :ime string?
            :adresa string?
            :timestamp inst?}]}}}
       :handler
       (fn [{{{:keys [vlasnik]} :path} :parameters}]
         (response/ok (adr/adrese-by-vlasnik vlasnik)))}}]]

   ["/adresa"
    ["/:post-id"
     {::auth/roles (auth/roles :adresa/get)
      :get {:parameters
            {:path
             {:post-id pos-int?}}
            :responses
            {200 {:adresa map?}
             403 {:adresa string?}
             404 {:adresa string?}
             500 {:adresa string?}}
            :handler
            (fn [{{{:keys [post-id]} :path} :parameters}]
              (if-some [post (adr/get-adresa post-id)]
                (response/ok
                 {:adresa post})
                (response/not-found
                 {:adresa "Post Not Found"})))}}]
    [""
     {::auth/roles (auth/roles :adrese/create!)
      :post
      {:parameters
       {:body ;; Data Spec for Request body parameters
        {:ime string?
         :adresa string?}}
       :responses
       {200
        {:body map?}
        400
        {:body map?}
        500
        {:errors map?}}
       :handler
       (fn [{{params :body} :parameters
             {:keys [identity]} :session}]
         (try
           (->> (adr/save-adresa! identity params)
                (assoc {:status :ok} :post)
                (response/ok))
           (catch Exception e
             (let [{id :nekretnine/error-id
                    errors :errors} (ex-data e)]
               (case id
                 :validation
                 (response/bad-request {:errors errors})
;;else
                 (response/internal-server-error
                  {:errors
                   {:server-error ["Failed to save message!"]}}))))))}}]]

   ["/login"
    {::auth/roles (auth/roles :auth/login)
     :post {:parameters
            {:body
             {:login string?
              :lozinka string?}}
            :responses
            {200
             {:body
              {:identity
               {:login string?
                :created_at inst?}}}
             401
             {:body
              {:message string?}}}
            :handler
            (fn [{{{:keys [login lozinka]} :body} :parameters
                  session :session}]
              (if-some [user (auth/authenticate-user login lozinka)]
                (->
                 (response/ok
                  {:identity user})
                 (assoc :session (assoc session
                                        :identity
                                        user)))
                (response/unauthorized
                 {:message "Incorrect login or lozinka."})))}}]
   ["/register"
    {::auth/roles (auth/roles :account/register)
     :post {:parameters
            {:body
             {:login string?
              :lozinka string?
              :confirm string?}}
            :responses
            {200
             {:body
              {:message string?}}
             400
             {:body
              {:message string?}}
             409
             {:body
              {:message string?}}}
            :handler
            (fn [{{{:keys [login lozinka confirm]} :body} :parameters}]
              (if-not (= lozinka confirm)
                (response/bad-request
                 {:message
                  "lozinka and Confirm do not match."})
                (try
                  (auth/create-user! login lozinka)
                  (response/ok
                   {:message
                    "User registration successful. Please log in."})
                  (catch clojure.lang.ExceptionInfo e
                    (if (= (:nekretnine/error-id (ex-data e))
                           ::auth/duplicate-user)
                      (response/conflict
                       {:message
                        "Registration failed! User with login already exists!"})
                      (throw e))))))}}]
   ["/logout"
    {::auth/roles (auth/roles :auth/logout)
     :post {:handler
            (fn [_]
              (->
               (response/ok)
               (assoc :session nil)))}}]

   ["/session"
    {::auth/roles (auth/roles :session/get)
     :get
     {:responses
      {200
       {:body
        {:session
         {:identity
          (ds/maybe
           {:login string?
            :created_at inst?
            :profile map?})}}}}
      :handler
      (fn [{{:keys [identity]} :session}]
        (response/ok
         {:session
          {:identity
           (not-empty
            (select-keys identity [:login :created_at :profile]))}}))}}]
   ["/vlasnik/:login"
    {::auth/roles (auth/roles :vlasnik/get)
     :get {:parameters
           {:path {:login string?}}
           :responses
           {200
            {:body map?}
            500
            {:errors map?}}
           :handler
           (fn [{{{:keys [login]} :path} :parameters}]
             (response/ok (vlasnik/get-vlasnik login)))}}]
   ["/my-account"
    ["/set-profile"
     {::auth/roles (auth/roles :account/set-profile!)
      :post {:parameters
             {:body
              {:profile map?}}
             :responses
             {200
              {:body map?}
              500
              {:errors map?}}
             :handler
             (fn [{{{:keys [profile]} :body} :parameters
                   {:keys [identity] :as session} :session}]
               (try
                 (let [identity
                       (vlasnik/set-vlasnik-profile (:login identity) profile)]
                   (update (response/ok {:success true})
                           :session
                           assoc :identity identity))
                 (catch Exception e
                   (log/error e)
                   (response/internal-server-error
                    {:errors {:server-error
                              ["Failed to set profile!"]}}))))}}]
    ["/media/upload"
     {::auth/roles (auth/roles :media/upload)
      :post
      {:parameters {:multipart (s/map-of keyword? multipart/temp-file-part)}
       :handler
       (fn [{{mp :multipart} :parameters
             {:keys [identity]} :session}]
         (response/ok
          (reduce-kv
           (fn [acc name {:keys [size content-type] :as file-part}]
             (cond
               (> size (* 5 1024 1024))
               (do
                 (log/error "File " name
                            " exceeded max size of 5 MB. (size: " size ")")
                 (update acc :failed-uploads (fnil conj []) name))
               (re-matches #"image/.*" content-type)
               (-> acc
                   (update :files-uploaded conj name)
                   (assoc name
                          (str "/api/media/"
                               (cond
                                 (= name :avatar)
                                 (media/insert-image-returning-name
                                  (assoc file-part
                                         :filename
                                         (str (:login identity) "_avatar.png"))
                                  {:width 128
                                   :height 128
                                   :owner (:login identity)})
                                 (= name :banner)
                                 (media/insert-image-returning-name
                                  (assoc file-part
                                         :filename
                                         (str (:login identity) "_banner.png"))
                                  {:width 1200
                                   :height 400
                                   :owner (:login identity)})
                                 :else
                                 (media/insert-image-returning-name
                                  (update
                                   file-part
                                   :filename
                                   string/replace #"\.[^\.]+$" ".png")
                                  {:max-width 800
                                   :max-height 2000
                                   :owner (:login identity)})))))
               :else
               (do
                 (log/error "Unsupported file type" content-type "for file" name)
                 (update acc :failed-uploads (fnil conj []) name))))
           {:files-uploaded []}
           mp)))}}]]
   ["/media/:name"
    {::auth/roles (auth/roles :media/get)
     :get {:parameters
           {:path {:name string?}}
           :handler (fn [{{{:keys [name]} :path} :parameters}]
                      (if-let [{:keys [data type]} (db/get-file {:name name})]
                        (-> (io/input-stream data)
                            (response/ok)
                            (response/content-type type))
                        (response/not-found)))}}]])