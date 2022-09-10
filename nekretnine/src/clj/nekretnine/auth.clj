(ns nekretnine.auth
  (:require
   [nekretnine.db.core :as db]
   [buddy.hashers :as hashers]
   [next.jdbc :as jdbc]))

(defn create-user! [login lozinka]
  (jdbc/with-transaction [t-conn db/*db*]
    (if-not (empty? (db/get-user-for-auth* t-conn {:login login}))
      (throw (ex-info "Korisnik vec postoji"
                      {:nekretnine/error-id ::duplicate-user
                       :error "Korisnik vec postoji"}))
      (db/create-user!* t-conn
                        {:login login
                         :lozinka (hashers/derive lozinka)}))))

(defn authenticate-user [login lozinka]
  (let [{hashed :lozinka :as user} (db/get-user-for-auth* {:login login})]
    (when (hashers/check lozinka hashed)
      (dissoc user :lozinka))))