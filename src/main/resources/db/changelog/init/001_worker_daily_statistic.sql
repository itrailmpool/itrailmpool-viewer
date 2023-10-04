create table worker_daily_statistic
(
    poolid           text                       not null,
    workername       text                       not null,
    date             date                       not null,
    average_hashrate double precision default 0 not null,
    accepted_shares  bigint           default 0 not null,
    rejected_shares  bigint           default 0 not null,
    total_reward     numeric(28, 12)  default 0 not null
);

create index idx_worker_daily_statistic
    on worker_daily_statistic (poolid, workername, date);