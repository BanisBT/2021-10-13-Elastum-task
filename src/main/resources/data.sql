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
       ('Paulius', 'Bareikis', '1978-05-23'),
       ('Danielius', 'Bareikis', '1990-05-23'),
       ('Laura', 'Bareikienė', '1978-05-23'),
       ('Egle', 'Bareikytė', '1978-05-23'),
       ('Jurga', 'Bareikytė', '1998-05-23'),
       ('Dovile', 'Bareikytė', '2010-05-23'),
       ('Dovile', 'Bareikytė-Barauskė', '2010-05-23'),
       ('Jurgis', 'Bareikis', '2010-05-23'),
       ('Nijole', 'Kliutis', '1998-05-23'),
       ('Aldona', 'Jura-Juoda', '1995-05-23'),
       ('Can not change surname for daughter seartch', 'Special person', '1900-01-01')
