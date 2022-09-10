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
   [spec-tools.data-spec :as ds]))



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
                 multipart/multipart-middleware]
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
    {:get
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
    {:post
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
      (fn [{{params :body} :parameters}]
        (try
          (adr/save-adresa! params)
          (response/ok {:status :ok})
          (catch Exception e
            (let [{id :nekretnine/error-id
                   errors :errors} (ex-data e)]
              (case id
                :validation
                (response/bad-request {:errors errors})
;;else
                (response/internal-server-error
                 {:errors
                  {:server-error ["Neuspesno cuvanje adrese"]}}))))))}}]

   ["/login"
    {:post {:parameters
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
    {:post {:parameters
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
    {:post {:handler
            (fn [_]
              (->
               (response/ok)
               (assoc :session nil)))}}]
   ["/session"
    {:get
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
