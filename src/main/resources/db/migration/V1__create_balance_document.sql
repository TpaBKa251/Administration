CREATE SCHEMA IF NOT EXISTS "administration";

CREATE TABLE "administration"."balance"
(
    "user"  UUID PRIMARY KEY NOT NULL UNIQUE,
    balance NUMERIC          NOT NULL,
    CONSTRAINT fk_balance_on_user FOREIGN KEY ("user") REFERENCES "user"."users" (id) ON DELETE CASCADE
);

CREATE TABLE "administration"."document"
(
    id         UUID PRIMARY KEY NOT NULL UNIQUE,
    "user"     UUID                                       NOT NULL,
    "type"     VARCHAR(50)                                NOT NULL,
    start_date DATE                                       NOT NULL,
    end_date   DATE                                       NOT NULL,
    CONSTRAINT fk_document_on_user FOREIGN KEY ("user") REFERENCES "user"."users" (id) ON DELETE CASCADE,
    CONSTRAINT uq_documents_user_and_type UNIQUE ("user", "type")
);
