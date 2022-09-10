CREATE TABLE oglasi
(
    id SERIAL PRIMARY KEY,
    ime text not null,
    adresa text not null,
    timestamp TIMESTAMP not null DEFAULT now()
);