create table minerstats
(
    id              bigserial                  not null
        constraint minerstats_pkey
            primary key,
    poolid          text                       not null,
    miner           text                       not null,
    worker          text                       not null,
    hashrate        double precision default 0 not null,
    sharespersecond double precision default 0 not null,
    created         timestamp with time zone   not null
);

alter table minerstats
    owner to miningcore;

create index idx_minerstats_pool_created
    on minerstats (poolid, created);

create index idx_minerstats_pool_miner_created
    on minerstats (poolid, miner, created);

create index idx_minerstats_pool_miner_worker_created_hashrate
    on minerstats (poolid asc, miner asc, worker asc, created desc, hashrate asc);

