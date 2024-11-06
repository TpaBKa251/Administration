CREATE TABLE balance
(
    "user"  UUID PRIMARY KEY NOT NULL UNIQUE,
    balance NUMERIC          NOT NULL
);

CREATE TABLE document
(
    id         UUID DEFAULT gen_random_uuid() PRIMARY KEY NOT NULL UNIQUE,
    "user"     UUID                                       NOT NULL,
    "type"     VARCHAR(50)                                NOT NULL,
    start_date DATE                                       NOT NULL,
    end_date   DATE                                       NOT NULL
);
