(ns nekretnine.routes.services
  (:require
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.coercion :as coercion]
   [reitit.coercion.spec :as spec-coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.parameters :as parameters]
   [nekretnine.adrese :as adr]
   [nekretnine.middleware :as middleware]
   [ring.util.http-response :as response]
   [nekretnine.middleware.formats :as formats]
   [nekretnine.auth :as auth]
   [spec-tools.data-spec :as ds]
   [nekretnine.auth.ring :refer [wrap-authorized get-roles-from-match]]
   [clojure.tools.logging :as log]))



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
                         route-roles)
                        (log/debug "User is unauthorized!"
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
   ["" {:no-doc true}
    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]
    ["/swagger-ui*"
     {:get (swagger-ui/create-swagger-ui-handler
            {:url "/api/swagger.json"})}]]
   ["/adrese"
    {::auth/roles (auth/roles :adrese/list)
     :get
     {:responses
      {200
       {:body ;; Data Spec for response body
        {:adrese
         [{:id pos-int?
           :ime string?
           :adresa string?
           :timestamp inst?}]}}}
      :handler
      (fn [_]
        (response/ok (adr/adresa-list)))}}]
   ["/adresa"
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
            (let [{id :guestbook/error-id
                   errors :errors} (ex-data e)]
              (case id
                :validation
                (response/bad-request {:errors errors})
;;else
                (response/internal-server-error
                 {:errors
                  {:server-error ["Netacan login ili lozinka"]}}))))))}}]
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
                 {:message "Netacan login ili lozinka."})))}}]
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
            :created_at inst?})}}}}
      :handler
      (fn [{{:keys [identity]} :session}]
        (response/ok {:session
                      {:identity
                       (not-empty
                        (select-keys identity [:login :created_at]))}}))}}]])
