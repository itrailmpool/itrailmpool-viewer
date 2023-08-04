package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerPaymentStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerShareStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.mapper.WorkerStatisticMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkerStatisticRepositoryImpl implements WorkerStatisticRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStatisticRepositoryImpl.class);
    private static final RowMapper<WorkerShareStatisticEntity> WORKER_SHARE_STATISTIC_ROW_MAPPER = getWorkerShareStatisticRowMapper();
    private static final RowMapper<WorkerHashRateStatisticEntity> WORKER_HASH_RATE_STATISTIC_ROW_MAPPER = getWorkerHashRateStatisticRowMapper();
    private static final RowMapper<WorkerPaymentStatisticEntity> WORKER_PAYMENT_STATISTIC_ROW_MAPPER = getWorkerPaymentStatisticRowMapper();
    private static final RowMapper<WorkerHashRateEntity> WORKER_HASH_RATE_ROW_MAPPER = getWorkerHashRateRowMapper();

    private final JdbcTemplate jdbcTemplate;
    private final WorkerStatisticMapper workerStatisticMapper;

    private static RowMapper<WorkerShareStatisticEntity> getWorkerShareStatisticRowMapper() {
        return (resultSet, i) -> {
            WorkerShareStatisticEntity workerShareStatistic = new WorkerShareStatisticEntity();

            workerShareStatistic.setWorkerName(resultSet.getString("worker"));
            workerShareStatistic.setTotalAcceptedShares(resultSet.getBigDecimal("total_valid_shares").toBigInteger());
            workerShareStatistic.setTotalRejectedShares(resultSet.getBigDecimal("total_invalid_shares").toBigInteger());
            workerShareStatistic.setDate(resultSet.getTimestamp("date").toInstant());

            return workerShareStatistic;
        };
    }

    private static RowMapper<WorkerHashRateStatisticEntity> getWorkerHashRateStatisticRowMapper() {
        return (resultSet, i) -> {
            WorkerHashRateStatisticEntity workerStatistic = new WorkerHashRateStatisticEntity();

            workerStatistic.setAverageHashRate(resultSet.getBigDecimal("total_average_hashrate"));
            workerStatistic.setAverageSharesPerSecond(resultSet.getBigDecimal("total_average_sharespersecond"));
            workerStatistic.setDate(resultSet.getTimestamp("date").toInstant());

            return workerStatistic;
        };
    }

    private static RowMapper<WorkerPaymentStatisticEntity> getWorkerPaymentStatisticRowMapper() {
        return (resultSet, i) -> {
            WorkerPaymentStatisticEntity workerPaymentStatistic = new WorkerPaymentStatisticEntity();

            workerPaymentStatistic.setTotalPayments(resultSet.getBigDecimal("total_payments"));
            workerPaymentStatistic.setDate(resultSet.getTimestamp("date").toInstant());

            return workerPaymentStatistic;
        };
    }


    private static RowMapper<WorkerHashRateEntity> getWorkerHashRateRowMapper() {
        return (resultSet, i) -> {
            WorkerHashRateEntity workerHashRateEntity = new WorkerHashRateEntity();

            workerHashRateEntity.setCurrentHashRate(resultSet.getBigDecimal("current_hashrate"));
            workerHashRateEntity.setHourlyAverageHashRate(resultSet.getBigDecimal("average_hashrate_hour"));
            workerHashRateEntity.setDailyAverageHashRate(resultSet.getBigDecimal("average_hashrate_day"));

            return workerHashRateEntity;
        };
    }

    @Override
    public List<WorkerStatisticEntity> getWorkerStatistic(String poolId, String workerName) {
        List<WorkerShareStatisticEntity> workerShareStatistics = jdbcTemplate.query("""
                        SELECT worker,
                               date_trunc('day', created)                   AS date,
                               sum(CASE WHEN isvalid THEN 1 ELSE 0 END)     AS total_valid_shares,
                               sum(CASE WHEN NOT isvalid THEN 1 ELSE 0 END) AS total_invalid_shares
                        FROM shares_statistic
                        WHERE worker = ?
                        GROUP BY date_trunc('day', created), worker
                        ORDER BY date DESC""",
                WORKER_SHARE_STATISTIC_ROW_MAPPER,
                workerName);

        List<WorkerHashRateStatisticEntity> workerHashRateStatistics = jdbcTemplate.query("""
                        SELECT date,
                               SUM(average_hashrate)        AS total_average_hashrate,
                               SUM(average_sharespersecond) AS total_average_sharespersecond
                        FROM (
                                 SELECT date_trunc('day', m.created) AS date,
                                        AVG(m.hashrate)              AS average_hashrate,
                                        AVG(m.sharespersecond)       AS average_sharespersecond
                                 FROM minerstats m
                                 WHERE m.poolid = ?
                                   AND m.worker IN (
                                     SELECT s.worker || '.' || s.device
                                     FROM shares_statistic s
                                     WHERE s.worker = ?
                                 )
                                 GROUP BY date_trunc('day', m.created), m.worker
                             ) AS subquery
                        GROUP BY date
                        ORDER BY date DESC""",
                WORKER_HASH_RATE_STATISTIC_ROW_MAPPER,
                poolId,
                workerName);

        List<WorkerPaymentStatisticEntity> workerPaymentStatistics = jdbcTemplate.query("""
                        SELECT date_trunc('day', p.created) AS date,
                               SUM(p.amount)                AS total_payments
                        FROM payments p
                                 JOIN miner_settings ms ON p.address = ms.address
                        WHERE ms.workername = ?
                        GROUP BY date_trunc('day', p.created)
                        ORDER BY date DESC;
                        """,
                WORKER_PAYMENT_STATISTIC_ROW_MAPPER,
                workerName);

        return workerStatisticMapper.toWorkerStatistic(workerHashRateStatistics, workerShareStatistics, workerPaymentStatistics);
    }

    @Override
    public WorkerHashRateEntity getWorkerHashRate(String poolId, String workerName) {
        try {
            return jdbcTemplate.queryForObject("""
                            SELECT sum(current_hashrate)      AS current_hashrate,
                                   sum(average_hashrate_hour) AS average_hashrate_hour,
                                   sum(average_hashrate_day)  AS average_hashrate_day
                            FROM
                                (SELECT (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - interval '90 seconds') AS current_hashrate,
                                        (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - interval '1 hour')     AS average_hashrate_hour,
                                        (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - interval '1 day')      AS average_hashrate_day
                                    FROM minerstats ms
                                    WHERE poolid = ?
                                      AND ms.created >= NOW() - interval '1 day'
                                  AND ms.worker IN (
                                        SELECT s.worker || '.' || s.device
                                        FROM shares_statistic s
                                        WHERE s.worker = ?
                                )
                                GROUP BY ms.worker) as subquery;""",
                    WORKER_HASH_RATE_ROW_MAPPER,
                    poolId,
                    workerName);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug("WorkerHashRate for poolId {} and workerName {} not found", poolId, workerName);
        }

        return null;
    }
}
