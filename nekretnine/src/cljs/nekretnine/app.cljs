(ns ^:dev/once nekretnine.app
  (:require
   [devtools.core :as devtools]
   [nekretnine.core :as core]))

(enable-console-print!)
(println "loading env/dev/cljs/nekretnine/app.cljs...")
(devtools/install!)
(core/init!)