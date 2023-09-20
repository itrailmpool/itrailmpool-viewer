create table poolstats
(
    id                   bigserial                  not null
        constraint poolstats_pkey
            primary key,
    poolid               text                       not null,
    connectedminers      integer          default 0 not null,
    poolhashrate         double precision default 0 not null,
    sharespersecond      double precision default 0 not null,
    networkhashrate      double precision default 0 not null,
    networkdifficulty    double precision default 0 not null,
    lastnetworkblocktime timestamp with time zone,
    blockheight          bigint           default 0 not null,
    connectedpeers       integer          default 0 not null,
    created              timestamp with time zone   not null
);

alter table poolstats
    owner to miningcore;

create index idx_poolstats_pool_created
    on poolstats (poolid, created);

