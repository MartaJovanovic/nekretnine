(ns ^:dev/once nekretnine.app
  (:require
   [devtools.core :as devtools]
   [nekretnine.core :as core]))


;;ignore println statements in prod
(set! *print-fn* (fn [& _]))
(core/init!)
