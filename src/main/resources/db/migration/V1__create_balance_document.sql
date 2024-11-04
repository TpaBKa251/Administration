CREATE TABLE balance (
                         "user" UUID PRIMARY KEY NOT NULL,
                         balance NUMERIC NOT NULL
);

CREATE TABLE document (
                          id UUID DEFAULT gen_random_uuid() PRIMARY KEY NOT NULL,
                          "user" UUID NOT NULL,
                          "type" VARCHAR(50) NOT NULL, -- Здесь указаны 50 символов, измените при необходимости
                          start_date DATE NOT NULL,
                          end_date DATE NOT NULL
);
