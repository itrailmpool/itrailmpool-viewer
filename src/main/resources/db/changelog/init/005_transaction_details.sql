create table transaction_details
(
    id                bigserial       not null,
    transaction_id    bigserial       not null,
    address           text            not null,
    amount            numeric(28, 12) not null default 0,
    constraint transaction_details_pkey primary key (id)
);

create index idx_transaction_details
    on transaction_details (transaction_id);