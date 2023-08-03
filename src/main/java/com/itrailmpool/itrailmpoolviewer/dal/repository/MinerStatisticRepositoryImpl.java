package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.AverageWorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerStatisticEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MinerStatisticRepositoryImpl implements MinerStatisticRepository {

    private static final RowMapper<MinerStatisticEntity> MINER_STATISTIC_ROW_MAPPER = getMinerStatisticRowMapper();
    private static final RowMapper<AverageWorkerStatisticEntity> AVERAGE_WORKER_STATISTIC_ROW_MAPPER = getAverageWorkerStatisticRowMapper();

    private final JdbcTemplate jdbcTemplate;

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
