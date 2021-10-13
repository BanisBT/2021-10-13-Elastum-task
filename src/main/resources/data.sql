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
