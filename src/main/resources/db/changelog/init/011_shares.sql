create table shares
(
    poolid            text                     not null,
    blockheight       bigint                   not null,
    difficulty        double precision         not null,
    networkdifficulty double precision         not null,
    miner             text                     not null,
    worker            text,
    useragent         text,
    ipaddress         text                     not null,
    source            text,
    created           timestamp with time zone not null
);

alter table shares
    owner to miningcore;

create index idx_shares_pool_miner
    on shares (poolid, miner);

create index idx_shares_pool_created
    on shares (poolid, created);

create index idx_shares_pool_miner_difficulty
    on shares (poolid, miner, difficulty);

