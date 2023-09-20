create table balances
(
    poolid  text                      not null,
    address text                      not null,
    amount  numeric(28, 12) default 0 not null,
    created timestamp with time zone  not null,
    updated timestamp with time zone  not null,
    constraint balances_pkey
        primary key (poolid, address)
);

alter table balances
    owner to miningcore;

