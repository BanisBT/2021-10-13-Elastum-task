DROP TABLE IF EXISTS person;

CREATE TABLE person
(
    id         BIGSERIAL PRIMARY KEY NOT NULL,
    name       VARCHAR(50)           NOT NULL,
    surname    VARCHAR(50)           NOT NULL,
    birth_date TIMESTAMP             NOT NULL,
    created    timestamp             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated    timestamp             NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO person(name, surname, birth_date)
VALUES ('Tomas', 'Barauskas', '1984-05-23'),
       ('Andrius', 'Bareikis', '1988-05-23'),
       ('Nijole', 'Kliutis', '1998-05-23'),
       ('Aldona', 'Jura-Juoda', '1995-05-23')
