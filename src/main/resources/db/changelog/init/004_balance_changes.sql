create table balance_changes
(
    id      bigserial                 not null
        constraint balance_changes_pkey
            primary key,
    poolid  text                      not null,
    address text                      not null,
    amount  numeric(28, 12) default 0 not null,
    usage   text,
    tags    text[],
    created timestamp with time zone  not null
);

alter table balance_changes
    owner to miningcore;

create index idx_balance_changes_pool_address_created
    on balance_changes (poolid asc, address asc, created desc);

create index idx_balance_changes_pool_tags
    on balance_changes using gin (tags);

