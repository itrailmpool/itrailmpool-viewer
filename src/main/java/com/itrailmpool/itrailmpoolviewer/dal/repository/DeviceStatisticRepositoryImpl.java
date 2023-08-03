package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceHashRateStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceSharesStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.mapper.DeviceStatisticMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeviceStatisticRepositoryImpl implements DeviceStatisticRepository {

    private static final RowMapper<DeviceSharesStatisticEntity> DEVICE_STATISTIC_ROW_MAPPER = getDeviceStatisticRowMapper();
    private static final RowMapper<DeviceHashRateStatisticEntity> DEVICE_HASH_RATE_STATISTIC_ROW_MAPPER = getDeviceHashRateStatisticRowMapper();

    private final JdbcTemplate jdbcTemplate;
    private final DeviceStatisticMapper deviceStatisticMapper;

    private static RowMapper<DeviceSharesStatisticEntity> getDeviceStatisticRowMapper() {
        return (resultSet, i) -> {
            DeviceSharesStatisticEntity deviceSharesStatistic = new DeviceSharesStatisticEntity();

            deviceSharesStatistic.setWorkerName(resultSet.getString("worker"));
            deviceSharesStatistic.setDeviceName(resultSet.getString("device"));
            deviceSharesStatistic.setLastValidShareDate(resultSet.getTimestamp("last_valid_share_date").toInstant());
            deviceSharesStatistic.setIsOnline(resultSet.getBoolean("is_online"));

            return deviceSharesStatistic;
        };
    }

    private static RowMapper<DeviceHashRateStatisticEntity> getDeviceHashRateStatisticRowMapper() {
        return (resultSet, i) -> {
            DeviceHashRateStatisticEntity deviceHashRateStatistic = new DeviceHashRateStatisticEntity();

            deviceHashRateStatistic.setWorkerName(resultSet.getString("worker"));
            deviceHashRateStatistic.setDeviceName(resultSet.getString("device"));
            deviceHashRateStatistic.setCurrentHashRate(resultSet.getBigDecimal("current_hashrate"));
            deviceHashRateStatistic.setHourlyAverageHashRate(resultSet.getBigDecimal("average_hashrate_hour"));
            deviceHashRateStatistic.setDailyAverageHashRate(resultSet.getBigDecimal("average_hashrate_day"));

            return deviceHashRateStatistic;
        };
    }

    @Override
    public List<DeviceStatisticEntity> getWorkerDevicesStatistic(String poolId, String workerName) {
        List<DeviceSharesStatisticEntity> deviceSharesStatistics = jdbcTemplate.query(
                """ 
                        SELECT s.worker,
                               s.device,
                               max(CASE WHEN s.isvalid = true THEN s.created END)                                 AS last_valid_share_date,
                               CASE WHEN max(s.created) >= NOW() - interval '90 seconds' THEN true ELSE false END AS is_online
                        FROM shares_statistic s
                        WHERE s.worker = ?
                        GROUP BY s.device, s.worker""",
                DEVICE_STATISTIC_ROW_MAPPER,
                workerName);

        //todo: this query execute more then 6 second - need to think how to increase performance
        List<DeviceHashRateStatisticEntity> deviceHashRateStatistics = jdbcTemplate.query("""
                        SELECT split_part(ms.worker, '.', 1) AS worker,
                               split_part(ms.worker, '.', 2) AS device,
                               (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - interval '90 seconds') AS current_hashrate,
                               (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - interval '1 hour') AS average_hashrate_hour,
                               (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - interval '1 day') AS average_hashrate_day
                        FROM minerstats ms
                        WHERE poolid = ?
                            AND ms.created >= NOW() - interval '1 day'
                            AND ms.worker IN (
                              SELECT s.worker || '.' || s.device
                              FROM shares_statistic s
                              WHERE s.worker = ?
                          )
                        GROUP BY ms.worker""",
                DEVICE_HASH_RATE_STATISTIC_ROW_MAPPER,
                poolId,
                workerName);

        return deviceStatisticMapper.toDeviceStatisticEntity(deviceSharesStatistics, deviceHashRateStatistics);
    }

    @Override
    public List<String> getWorkerDevices(String workerName) {
        return jdbcTemplate.queryForList("SELECT DISTINCT device FROM shares_statistic WHERE worker = ?", String.class, workerName);
    }

    @Override
    public Integer getActiveWorkersCount(String poolId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT s.worker) FROM shares s WHERE s.poolid = ? AND created >= NOW() - INTERVAL '90 seconds'",
                Integer.class,
                poolId);
    }
}
