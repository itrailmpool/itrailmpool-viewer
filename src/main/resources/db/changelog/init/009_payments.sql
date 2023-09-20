create table payments
(
    id                          bigserial                not null
        constraint payments_pkey
            primary key,
    poolid                      text                     not null,
    coin                        text                     not null,
    address                     text                     not null,
    amount                      numeric(28, 12)          not null,
    transactionconfirmationdata text                     not null,
    created                     timestamp with time zone not null
);

alter table payments
    owner to miningcore;

create index idx_payments_pool_coin_wallet
    on payments (poolid, coin, address);

