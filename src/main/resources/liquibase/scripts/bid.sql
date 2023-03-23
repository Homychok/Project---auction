-- liquibase formatted sql

-- changeset Homychok:1
CREATE TABLE bid (
                         id  BIGINT PRIMARY KEY,
                         bidder_name VARCHAR(64) NOT NULL,
                         bid_date TIMESTAMP,
                         lot_id  BIGINT REFERENCES lot(id)
);

ALTER TABLE bid ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY;

