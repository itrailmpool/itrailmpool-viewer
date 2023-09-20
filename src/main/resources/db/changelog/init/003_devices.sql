create table devices
(
    id                bigserial   not null,
    "name"            text        not null,
    worker_id         bigserial   not null,
    creation_date     timestamptz not null default CURRENT_TIMESTAMP,
    modification_date timestamptz not null default CURRENT_TIMESTAMP,
    is_enabled        boolean     not null default true,
    constraint devices_pkey primary key (id)
);