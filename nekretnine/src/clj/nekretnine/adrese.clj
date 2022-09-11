(ns nekretnine.adrese
  (:require
   [nekretnine.db.core :as db]
   [nekretnine.validation :refer [validate-adresa]]))

(defn adresa-list []
  {:adrese (vec (db/get-adrese))})

(defn save-adresa! [{:keys [login]} adresa]
  (if-let [errors (validate-adresa adresa)]
    (throw (ex-info "Adresa nije validna"
                    {:guestbook/error-id :validation
                     :errors errors}))
    (db/save-adresa! (assoc adresa :vlasnik login))))

;; (defn adrese-by-vlasnik [vlasnik]
;;   {:adrese (vec (db/get-adrese-by-vlasnik {:vlasnik vlasnik}))})