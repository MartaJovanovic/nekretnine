(ns nekretnine.vlasnik
  (:require [nekretnine.db.core :as db]))

(defn get-vlasnik [login]
  (db/get-user* {:login login}))

(defn set-vlasnik-profile [login profile]
  (db/set-profile-for-user* {:login login
                             :profile profile}))