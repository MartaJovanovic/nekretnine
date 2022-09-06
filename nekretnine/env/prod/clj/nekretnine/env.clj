(ns nekretnine.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[nekretnine started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[nekretnine has shut down successfully]=-"))
   :middleware identity})
