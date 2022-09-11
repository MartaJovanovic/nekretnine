-- :name save-adresa! :<! :1
-- :doc creates a new adresa using the ime and adresa keys
INSERT INTO oglasi
    (vlasnik, ime, adresa)
VALUES
    (:vlasnik, :ime, :adresa)
RETURNING *;
-- :name get-adrese :? :*
-- :doc selects all available adresas
SELECT *
from oglasi

-- :name create-user!* :! :n
-- :doc creates a new user with the provided login and hashed lozinka
INSERT INTO users
    (login, lozinka)
VALUES
    (:login, :lozinka)
-- :name get-user-for-auth* :? :1
-- :doc selects a user for authentication
SELECT *
FROM users
WHERE login = :login