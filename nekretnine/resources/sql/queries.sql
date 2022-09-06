-- :name save-adresa! :! :n
-- :doc kreira novu adresu
INSERT INTO nekretnine
    (ime, adresa)
VALUES
    (:ime, :adresa)
-- :name get-adrese :? :*
-- :doc vraca sve adrese
SELECT *
from nekretnine

