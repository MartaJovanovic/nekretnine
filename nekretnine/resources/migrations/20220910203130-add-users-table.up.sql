CREATE TABLE users
(
    login text PRIMARY KEY,
    lozinka text not null,
    created_at TIMESTAMP not null DEFAULT now()
);
