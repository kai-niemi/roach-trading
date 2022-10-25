--
-- Product schema for CockroachDB
--

-- drop table if exists product cascade;

-- drop sequence if exists product_seq;
create sequence if not exists product_seq
    start 1 increment by 1 cache 64;

create table product
(
    id         uuid           not null default gen_random_uuid(),
    buy_price  numeric(19, 2) not null,
    sell_price numeric(19, 2) not null,
    currency   varchar(3)     not null,
    reference  varchar(128)   not null,

    primary key (id)
);

-- drop index uidx_product_ref;
create unique index uidx_product_ref on product (reference)
    using hash with bucket_count =8
    storing (buy_price,sell_price,currency);
