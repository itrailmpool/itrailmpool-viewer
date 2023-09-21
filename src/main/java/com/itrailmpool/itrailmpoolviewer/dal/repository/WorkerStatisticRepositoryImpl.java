package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerPaymentStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerShareStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.mapper.WorkerStatisticMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkerStatisticRepositoryImpl implements WorkerStatisticRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStatisticRepositoryImpl.class);
    private static final RowMapper<WorkerShareStatisticEntity> WORKER_SHARE_STATISTIC_ROW_MAPPER = getWorkerShareStatisticRowMapper();
    private static final RowMapper<WorkerHashRateStatisticEntity> WORKER_HASH_RATE_STATISTIC_ROW_MAPPER = getWorkerHashRateStatisticRowMapper();
    private static final RowMapper<WorkerPaymentStatisticEntity> WORKER_PAYMENT_STATISTIC_ROW_MAPPER = getWorkerPaymentStatisticRowMapper();
    private static final RowMapper<WorkerHashRateEntity> WORKER_HASH_RATE_ROW_MAPPER = getWorkerHashRateRowMapper();
    private static final RowMapper<WorkerStatisticEntity> WORKER_STATISTIC_ROW_MAPPER = getWorkerStatisticRowMapper();

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final WorkerStatisticMapper workerStatisticMapper;
    @Value("${app.pool.statistic.worker.online.check.interval:600}")
    private Integer workerOnlineCheckInterval;
    @Value("${app.pool.statistic.worker.statistic.max_period:3}")
    private Integer workerDailyStatisticMaxPeriod;

    private static RowMapper<WorkerShareStatisticEntity> getWorkerShareStatisticRowMapper() {
        return (resultSet, i) -> {
            WorkerShareStatisticEntity workerShareStatistic = new WorkerShareStatisticEntity();

            workerShareStatistic.setPoolId(resultSet.getString("poolid"));
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

    private static RowMapper<WorkerStatisticEntity> getWorkerStatisticRowMapper() {
        return (resultSet, i) -> {
            WorkerStatisticEntity shareStatisticEntity = new WorkerStatisticEntity();

            shareStatisticEntity.setPoolId(resultSet.getString("poolid"));
            shareStatisticEntity.setWorkerName(resultSet.getString("workername"));
            shareStatisticEntity.setDate(resultSet.getDate("date").toLocalDate());
            shareStatisticEntity.setAverageHashRate(resultSet.getBigDecimal("average_hashrate"));
            shareStatisticEntity.setTotalAcceptedShares(resultSet.getBigDecimal("accepted_shares").toBigInteger());
            shareStatisticEntity.setTotalRejectedShares(resultSet.getBigDecimal("rejected_shares").toBigInteger());
            shareStatisticEntity.setTotalPayment(resultSet.getBigDecimal("total_reward"));

            return shareStatisticEntity;
        };
    }

    @Override
    public List<WorkerStatisticEntity> getWorkerStatistic(String poolId, String workerName) {
        LocalDate lastWorkerDailyStatisticDate = getLastWorkerDailyStatisticDate();

        List<WorkerStatisticEntity> workerDailyStatistic = getWorkerDailyStatistic(poolId, workerName, null, lastWorkerDailyStatisticDate);
        List<WorkerStatisticEntity> workerStatisticFromDate = getWorkerStatisticFromDate(poolId, workerName, lastWorkerDailyStatisticDate);

        return ListUtils.union(workerDailyStatistic, workerStatisticFromDate);
    }

    @Override
    public List<WorkerStatisticEntity> getWorkerStatisticFromDate(String poolId, String workerName, LocalDate fromDate) {
        List<WorkerShareStatisticEntity> workerShareStatistics = getWorkerShareStatistics(poolId, workerName, fromDate);
        List<WorkerHashRateStatisticEntity> workerHashRateStatistics = getWorkerHashRateStatistic(poolId, workerName, fromDate);
        List<WorkerPaymentStatisticEntity> workerPaymentStatistics = getWorkerPaymentStatistic(poolId, workerName, fromDate);

        return workerStatisticMapper.toWorkerStatistic(workerHashRateStatistics, workerShareStatistics, workerPaymentStatistics);
    }

    @Override
    public WorkerHashRateEntity getWorkerHashRate(String poolId, String workerName) {
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("workerOnlineCheckInterval", workerOnlineCheckInterval);
            parameters.addValue("poolId", poolId);
            parameters.addValue("worker", workerName);

            return namedParameterJdbcTemplate.queryForObject("""
                            SELECT sum(current_hashrate)      AS current_hashrate,
                                   sum(average_hashrate_hour) AS average_hashrate_hour,
                                   sum(average_hashrate_day)  AS average_hashrate_day
                            FROM
                                (SELECT (SELECT avg(hashrate) FROM minerstats WHERE poolid = :poolId AND worker = ms.worker AND created >= NOW() - make_interval(secs => :workerOnlineCheckInterval)) AS current_hashrate,
                                        (SELECT avg(hashrate) FROM minerstats WHERE poolid = :poolId AND worker = ms.worker AND created >= NOW() - interval '1 hour')                                 AS average_hashrate_hour,
                                        (SELECT avg(hashrate) FROM minerstats WHERE poolid = :poolId AND worker = ms.worker AND created >= NOW() - interval '1 day')                                  AS average_hashrate_day
                                    FROM minerstats ms
                                    WHERE poolid = :poolId
                                      AND ms.created >= NOW() - interval '1 day'
                                  AND ms.worker IN (
                                    SELECT w.name || '.' || d.name
                                    FROM devices d
                                    INNER JOIN workers w ON d.worker_id = w.id
                                    WHERE w.name = :worker
                                )
                                GROUP BY ms.worker) as subquery;""",
                    parameters,
                    WORKER_HASH_RATE_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug("WorkerHashRate for poolId {} and workerName {} not found", poolId, workerName);
        }

        return null;
    }

    @Override
    public LocalDate getLastWorkerDailyStatisticDate() {
        try {
            return jdbcTemplate.queryForObject("SELECT MAX(date) FROM worker_daily_statistic", LocalDate.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void saveAll(List<WorkerStatisticEntity> workerStatisticEntities) {
        try {
            jdbcTemplate.batchUpdate("""
                            INSERT INTO worker_daily_statistic (poolid, workername, date, average_hashrate, accepted_shares, rejected_shares, total_reward) 
                            VALUES (?, ?, ?, ?, ?, ?, ?)""",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            WorkerStatisticEntity entity = workerStatisticEntities.get(i);
                            ps.setString(1, entity.getPoolId());
                            ps.setString(2, entity.getWorkerName());
                            ps.setObject(3, java.sql.Date.valueOf(entity.getDate()));
                            ps.setObject(4, entity.getAverageHashRate());
                            ps.setObject(5, entity.getTotalAcceptedShares(), java.sql.Types.BIGINT);
                            ps.setObject(6, entity.getTotalRejectedShares(), java.sql.Types.BIGINT);
                            ps.setBigDecimal(7, entity.getTotalPayment());
                        }

                        @Override
                        public int getBatchSize() {
                            return workerStatisticEntities.size();
                        }
                    });
        } catch (DataAccessException e) {
            LOGGER.error("Unable to insert new values", e);
        }
    }

    private List<WorkerShareStatisticEntity> getWorkerShareStatistics(String poolId, String workerName, LocalDate fromDate) {
        if (fromDate == null) {
            return jdbcTemplate.query("""
                            SELECT worker,
                                   poolid,
                                   date_trunc('day', created)                   AS date,               
                                   sum(CASE WHEN isvalid THEN 1 ELSE 0 END)     AS total_valid_shares,
                                   sum(CASE WHEN NOT isvalid THEN 1 ELSE 0 END) AS total_invalid_shares
                            FROM shares_statistic
                            WHERE 
                                worker = ? AND 
                                poolid = ?
                            GROUP BY date_trunc('day', created), worker, poolid
                            ORDER BY date DESC""",
                    WORKER_SHARE_STATISTIC_ROW_MAPPER,
                    workerName,
                    poolId);
        }

        return jdbcTemplate.query("""
                        SELECT worker,
                               poolid,
                               date_trunc('day', created)                   AS date,               
                               sum(CASE WHEN isvalid THEN 1 ELSE 0 END)     AS total_valid_shares,
                               sum(CASE WHEN NOT isvalid THEN 1 ELSE 0 END) AS total_invalid_shares
                        FROM shares_statistic
                        WHERE 
                            worker = ? AND 
                            poolid = ? AND 
                            date_trunc('day', created) > ?
                        GROUP BY date_trunc('day', created), worker, poolid
                        ORDER BY date DESC""",
                WORKER_SHARE_STATISTIC_ROW_MAPPER,
                workerName,
                poolId,
                fromDate);
    }

    private List<WorkerHashRateStatisticEntity> getWorkerHashRateStatistic(String poolId, String workerName, LocalDate fromDate) {
        if (fromDate == null) {
            return jdbcTemplate.query("""
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
        }

        return jdbcTemplate.query("""
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
                                 ) AND m.created >= ?
                                 GROUP BY date_trunc('day', m.created), m.worker
                             ) AS subquery
                        GROUP BY date
                        ORDER BY date DESC""",
                WORKER_HASH_RATE_STATISTIC_ROW_MAPPER,
                poolId,
                workerName,
                fromDate);
    }

    private List<WorkerPaymentStatisticEntity> getWorkerPaymentStatistic(String poolId, String workerName, LocalDate fromDate) {
        if (fromDate == null) {
            return jdbcTemplate.query("""
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
        }

        return jdbcTemplate.query("""
                        SELECT date_trunc('day', p.created) AS date,
                               SUM(p.amount)                AS total_payments
                        FROM payments p
                                 JOIN miner_settings ms ON p.address = ms.address
                        WHERE ms.workername = ? AND p.created > ?
                        GROUP BY date_trunc('day', p.created)
                        ORDER BY date DESC;
                        """,
                WORKER_PAYMENT_STATISTIC_ROW_MAPPER,
                workerName,
                fromDate);
    }

    private List<WorkerStatisticEntity> getWorkerDailyStatistic(String poolId, String workerName, LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null) {
            fromDate = getMinWorkerDailyStatisticDate();
        }

        if (toDate == null) {
            toDate = LocalDate.now();
        }

        return jdbcTemplate.query("""
                        SELECT *
                        FROM worker_daily_statistic
                        WHERE 
                            poolid = ? AND 
                            workername = ? AND 
                            date BETWEEN ? AND ?
                        ORDER BY date DESC""",
                WORKER_STATISTIC_ROW_MAPPER,
                poolId,
                workerName,
                fromDate,
                toDate);
    }

    private LocalDate getMinWorkerDailyStatisticDate() {
        return LocalDate.now().minusMonths(workerDailyStatisticMaxPeriod);
    }
}
