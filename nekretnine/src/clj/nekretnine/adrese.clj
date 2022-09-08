(ns nekretnine.adrese
  (:require
   [nekretnine.db.core :as db]
   [nekretnine.validation :refer [validate-adresa]]))

(defn adresa-list []
  {:adrese (vec (db/get-adrese))})

(defn save-adresa! [adresa]
  (if-let [errors (validate-adresa adresa)]
    (throw (ex-info "Adresa nije validna"
                    {:nekretnine/error-id :validation
                     :errors errors}))
    (db/save-adresa! adresa)))