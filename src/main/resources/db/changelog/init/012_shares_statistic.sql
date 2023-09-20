create table shares_statistic
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
    created           timestamp with time zone not null,
    isvalid           boolean,
    device            text
);

alter table shares_statistic
    owner to miningcore;

create index idx_shares_statistic_worker
    on shares_statistic (worker);

create index idx_shares_statistic_worker_created
    on shares_statistic (worker, created);

