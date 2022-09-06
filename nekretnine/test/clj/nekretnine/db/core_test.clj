(ns nekretnine.db.core-test
  (:require
   [nekretnine.db.core :refer [*db*] :as db]
   [java-time.pre-java8]
   [luminus-migrations.core :as migrations]
   [clojure.test :refer :all]
   [next.jdbc :as jdbc]
   [nekretnine.config :refer [env]]
   [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'nekretnine.config/env
     #'nekretnine.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-adrese
  (jdbc/with-transaction [t-conn *db* {:rollback-only true}]
    (is (= 1 (db/save-adresa!
              t-conn
              {:ime "Marko Petrovic"
               :adresa "Dalmatinska 25"}
              {:connection t-conn})))
    (is (= {:ime "Marko Petrovic"
            :adresa "Dalmatinska 25"}
           (-> (db/get-adrese t-conn {})
               (first)
               (select-keys [:ime :adresa]))))))
