alter table "administration"."balance"
    add column version bigint not null default 0;

alter table "administration"."document"
    add column version bigint not null default 0;