(ns nekretnine.auth.ws
  (:require
   [nekretnine.auth :as auth]))

(defn authorized? [roles-by-id adr]
  (boolean
   (some (roles-by-id (:id adr) #{})
         (-> adr
             :session
             :identity
             (auth/identity->roles)))))