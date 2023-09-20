create table miner_settings
(
    poolid           text                     not null,
    address          text                     not null,
    paymentthreshold numeric(28, 12)          not null,
    created          timestamp with time zone not null,
    updated          timestamp with time zone not null,
    workername       text                     not null,
    password         text,
    constraint miner_settings_pkey
        primary key (poolid, workername)
);

alter table miner_settings
    owner to miningcore;

create index idx_miner_settings
    on miner_settings (poolid, workername, password);

