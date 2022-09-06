(ns nekretnine.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [nekretnine.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[nekretnine started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[nekretnine has shut down successfully]=-"))
   :middleware wrap-dev})
