CREATE SCHEMA IF NOT EXISTS "administration";

CREATE TABLE "administration"."balance"
(
    "user"  UUID PRIMARY KEY NOT NULL UNIQUE,
    balance NUMERIC          NOT NULL
);

CREATE TABLE "administration"."document"
(
    id         UUID PRIMARY KEY NOT NULL UNIQUE,
    "user"     UUID                                       NOT NULL UNIQUE ,
    "type"     VARCHAR(50)                                NOT NULL,
    start_date DATE                                       NOT NULL,
    end_date   DATE                                       NOT NULL,
    CONSTRAINT uq_documents_user_and_type UNIQUE ("user", "type"),
    CONSTRAINT chk_date_valid CHECK (end_date > start_date)
);
