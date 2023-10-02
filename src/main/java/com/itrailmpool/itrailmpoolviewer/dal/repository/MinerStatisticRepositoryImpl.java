package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.AverageWorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerPerformanceStatsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MinerStatisticRepositoryImpl implements MinerStatisticRepository {

    private static final RowMapper<MinerStatisticEntity> MINER_STATISTIC_ROW_MAPPER = getMinerStatisticRowMapper();
    private static final RowMapper<AverageWorkerStatisticEntity> AVERAGE_WORKER_STATISTIC_ROW_MAPPER = getAverageWorkerStatisticRowMapper();
    private static final RowMapper<WorkerPerformanceStatsEntity> WORKER_PERFORMANCE_STATISTIC_ROW_MAPPER = getWorkerPerformanceStatsEntityRowMapper();

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<AverageWorkerStatisticEntity> getWorkerAverageHashRateAfterDate(String worker, List<String> workerDevices, Instant dateFrom) {
        return jdbcTemplate.query("""
                        SELECT date,
                               SUM(average_hashrate)        AS total_average_hashrate,
                               SUM(average_sharespersecond) AS total_average_sharespersecond
                        FROM (
                                 SELECT date_trunc('day', m.created) AS date,
                                        AVG(m.hashrate)              AS average_hashrate,
                                        AVG(m.sharespersecond)       AS average_sharespersecond
                                 FROM minerstats m
                                 WHERE m.worker IN (?) AND m.created >= ?
                                 GROUP BY date_trunc('day', m.created), m.worker
                             ) AS subquery
                        GROUP BY date
                        ORDER BY date DESC""",
                AVERAGE_WORKER_STATISTIC_ROW_MAPPER,
                workerDevices,
                dateFrom);
    }

    @Override
    public List<WorkerPerformanceStatsEntity> getWorkerPerformance(String poolId, String address, String workerName) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("poolId", poolId);
        parameters.addValue("address", address);
        parameters.addValue("workerNameLike", workerName + "%");

        return namedParameterJdbcTemplate.query("""
                        WITH statsByaddress AS (
                            SELECT worker,
                                   date_trunc('hour', created) AS created,
                                   AVG(hashrate)               AS hashrate,
                                   AVG(sharespersecond)        AS sharespersecond
                            FROM minerstats
                            WHERE poolid = :poolId
                              AND miner = :address
                              AND created >= now() - interval '3 day'
                              AND created <= now()
                            GROUP BY date_trunc('hour', created), worker
                            ORDER BY created, worker
                        )
                        SELECT * FROM statsByaddress
                        WHERE worker LIKE :workerNameLike;""",
                parameters,
                WORKER_PERFORMANCE_STATISTIC_ROW_MAPPER);
    }

    private static RowMapper<MinerStatisticEntity> getMinerStatisticRowMapper() {
        return (resultSet, i) -> {
            MinerStatisticEntity minerStatistic = new MinerStatisticEntity();

            minerStatistic.setPoolId(resultSet.getString("poolid"));
            minerStatistic.setMiner(resultSet.getString("miner"));
            minerStatistic.setWorkerDeviceCode(resultSet.getString("worker"));
            minerStatistic.setHashRate(resultSet.getBigDecimal("hashrate"));
            minerStatistic.setSharePerSecond(resultSet.getBigDecimal("sharespersecond"));
            minerStatistic.setCreated(resultSet.getTimestamp("created").toInstant());

            return minerStatistic;
        };
    }

    private static RowMapper<WorkerPerformanceStatsEntity> getWorkerPerformanceStatsEntityRowMapper() {
        return (resultSet, i) -> {
            WorkerPerformanceStatsEntity workerPerformanceStats = new WorkerPerformanceStatsEntity();

            workerPerformanceStats.setWorkerDeviceKey(resultSet.getString("worker"));
            workerPerformanceStats.setHashRate(resultSet.getBigDecimal("hashrate"));
            workerPerformanceStats.setSharesPerSecond(resultSet.getBigDecimal("sharespersecond"));
            workerPerformanceStats.setCreated(resultSet.getTimestamp("created").toInstant());

            return workerPerformanceStats;
        };
    }

    private static RowMapper<AverageWorkerStatisticEntity> getAverageWorkerStatisticRowMapper() {
        return (resultSet, i) -> {
            AverageWorkerStatisticEntity averageWorkerStatistic = new AverageWorkerStatisticEntity();

            averageWorkerStatistic.setDate(resultSet.getTimestamp("date").toInstant());
            averageWorkerStatistic.setAverageHashRate(resultSet.getBigDecimal("total_average_hashrate"));
            averageWorkerStatistic.setAverageSharePerSecond(resultSet.getBigDecimal("total_average_sharespersecond"));

            return averageWorkerStatistic;
        };
    }

}
