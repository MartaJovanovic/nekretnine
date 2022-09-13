(ns nekretnine.adrese
  (:require
   [nekretnine.db.core :as db]
   [nekretnine.validation :refer [validate-adresa]]
   [conman.core :as conman]))

(defn adresa-list []
  {:adrese (vec (db/get-adrese))})

(defn save-adresa! [{:keys [login]} adresa]
  (if-let [errors (validate-adresa adresa)]
    (throw (ex-info "Adresa nije validna"
                    {:neretnine/error-id :validation
                     :errors errors}))
    (let [tags (map second
                    (re-seq #"(?<=\s|^)#([-\w]+)(?=\s|$)"
                            (:adresa adresa)))]

      (conman/with-transaction [db/*db*]
        (let [post-id (:id
                       (db/save-adresa! db/*db*
                                        (assoc adresa
                                               :vlasnik login
                                               :parent (:parent adresa))))]
          (db/get-timeline-post db/*db* {:post post-id
                                         :user login
                                         :is_boost false}))))))

(defn adrese-by-vlasnik [vlasnik]
  {:adrese (vec (db/get-adrese-by-vlasnik {:vlasnik vlasnik}))})

(defn get-adresa [post-id]
  (db/get-adresa {:id post-id}))

(defn boost-message [{:keys [login]} post-id poster]
  (conman/with-transaction [db/*db*]
    (db/boost-post! db/*db* {:post post-id
                             :poster poster
                             :user login})
    (db/get-timeline-post db/*db* {:post post-id
                                   :user login
                                   :is_boost true})))
(defn timeline []
  {:adrese (vec (db/get-timeline))})

(defn timeline-for-poster [poster]
  {:adrese (vec (db/get-timeline-for-poster {:poster poster}))})
