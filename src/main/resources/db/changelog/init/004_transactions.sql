create table transactions
(
    id                bigserial       not null,
    poolid            text            not null,
    hash              text            not null,
    amount            numeric(28, 12) not null default 0,
    creation_date     timestamptz     not null default CURRENT_TIMESTAMP,
    modification_date timestamptz     not null default CURRENT_TIMESTAMP,
    constraint transactions_pkey primary key (id)
);

create index idx_transactions
    on transactions (poolid, hash);