create table workers
(
    id            bigserial   not null,
    "name"        text        not null,
    poolid        text        not null,
    creation_date timestamptz not null default CURRENT_TIMESTAMP,
    constraint workers_pkey primary key (id)
);

create unique index if not exists workers_pool_id_unq_idx on workers (name, poolid);