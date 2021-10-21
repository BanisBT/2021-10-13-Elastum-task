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
VALUES ('Can not change surname for daughter seartch', 'Special person', '1900-01-01'),
       ('Tomas', 'Barauskas', '1984-05-23'),
       ('Tinka', 'Barauskienė', '1964-05-23'),
       ('Netinka', 'Barauskienėnienė', '1964-05-23'),
       ('Aldona', 'Jurgytė-Juodienė', '1995-05-23'),
       ('Nijole', 'Kliutaitė', '1998-05-23'),
       ('Nijole', 'Kliutaitytė', '1998-05-23'),
       ('Nijole', 'Sakutė', '1998-05-23'),
       ('Nijole', 'Svirplienė', '1998-05-23'),
       ('Person', 'Bareikis', '1988-05-23'),
       ('BrolisVyresnis', 'Bareikis', '1978-05-23'),
       ('BrolisVyresnis', 'Bareikis', '1990-05-23'),
       ('Zmona', 'Bareikienė', '1978-03-23'),
       ('ZmonaDu', 'Bareikienė', '1978-03-23'),
       ('SesuoVyresne', 'Bareikytė', '1978-05-23'),
       ('SesuoJaunesne', 'Bareikytė', '1998-05-23'),
       ('Dukte', 'Bareikytė', '2010-05-23'),
       ('DukteSuDvigubaPavarde', 'Bareikytė-Barauskė', '2010-05-23'),
       ('Sunus', 'Bareikis', '2010-05-23'),
       ('Tevas', 'Bareikis', '1958-05-23'),
       ('Anukas', 'Bareikis', '2040-05-23'),
       ('PersonBetweenDate', 'right', '2077-05-23'),
       ('PersonBetweenDate', 'notRight', '2077-05-23'),
       ('1', '5', '2099-05-23'),
       ('2', '8', '2098-05-23'),
       ('3', '4', '2049-05-23'),
       ('4', '3', '2097-05-23'),
       ('5', '1', '2096-05-23'),
       ('6', '2', '2044-05-23')




