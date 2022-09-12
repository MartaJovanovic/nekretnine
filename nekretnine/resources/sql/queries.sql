-- :name save-adresa! :<! :1
-- :doc creates a new adresa using the ime and adresa keys
INSERT INTO oglasi
    (vlasnik, ime, adresa)
VALUES
    (:vlasnik, :ime, :adresa)
RETURNING *;
-- :name get-adrese :? :*
-- :doc selects all available adresas
SELECT
    p.id as id,
    p.timestamp as timestamp,
    p.adresa as adresa,
    p.ime as ime,
    p.vlasnik as vlasnik,
    a.profile->>'avatar'
as avatar
from oglasi as p join users as a
on a.login = p.vlasnik
-- :name get-adresa :? :1
-- :doc selects a message
SELECT
    p.id as id,
    p.timestamp as timestamp,
    p.adresa as adresa,
    p.ime as ime,
    p.vlasnik as vlasnik,
    a.profile->>'avatar'
as avatar
from oglasi as p join users as a
on a.login = p.vlasnik
where p.id = :id
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
-- :name get-adrese-by-vlasnik :? :*
-- :doc svi oglasi vlasnika
SELECT *
from oglasi
WHERE vlasnik = :vlasnik
-- :name set-profile-for-user* :<! :1
-- :doc sets a profile map for the specified user
UPDATE users
SET profile = :profile
where :login = login
RETURNING *;
-- :name get-user* :? :1
-- :doc gets a user's publicly available information
SELECT login, created_at, profile
from users
WHERE login = :login
-- :name save-file! :! :n
-- saves a file to the database
INSERT INTO media
    (name, type, owner, data)
VALUES
    (:name, :type, :owner, :data)
ON CONFLICT
(name) DO
UPDATE
SET type = :type,
data
= :data
WHERE media.owner = :owner
-- :name get-file :? :1
-- Gets a file from the database
select *
from media
where name = :name