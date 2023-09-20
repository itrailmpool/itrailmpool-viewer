package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceHashRateStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceSharesStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.mapper.DeviceStatisticMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DeviceStatisticRepositoryImpl implements DeviceStatisticRepository {

    private static final RowMapper<DeviceSharesStatisticEntity> DEVICE_STATISTIC_ROW_MAPPER = getDeviceStatisticRowMapper();
    private static final RowMapper<DeviceHashRateStatisticEntity> DEVICE_HASH_RATE_STATISTIC_ROW_MAPPER = getDeviceHashRateStatisticRowMapper();
    private static final RowMapper<DeviceEntity> DEVICE_ENTITY_ROW_MAPPER = getDeviceRowMapper();

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DeviceStatisticMapper deviceStatisticMapper;
    @Value("${app.pool.statistic.device.online.check.interval:90}")
    private Integer deviceOnlineCheckInterval;

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

    private static RowMapper<DeviceEntity> getDeviceRowMapper() {
        return (resultSet, i) -> {
            DeviceEntity deviceEntity = new DeviceEntity();

            deviceEntity.setId(resultSet.getLong("id"));
            deviceEntity.setName(resultSet.getString("name"));
            deviceEntity.setWorkerId(resultSet.getLong("worker_id"));
            deviceEntity.setCreated(resultSet.getTimestamp("creation_date").toInstant());
            deviceEntity.setModified(resultSet.getTimestamp("modification_date").toInstant());
            deviceEntity.setEnabled(resultSet.getBoolean("is_enabled"));

            return deviceEntity;
        }
    }

    private static String findDevicesByWorkerName() {
        return """
                SELECT d."name"
                FROM devices d 
                INNER JOIN workers w ON d.worker_id = w.id 
                WHERE w."name" = ? AND d.is_enabled = true
                """;
    }

//    private static List<String> findDevicesByWorkerNameAndCreationDate() {
//        return """
//                SELECT d."name"
//                FROM devices d
//                INNER JOIN workers w ON d.worker_id = w.id
//                WHERE w."name" = ? AND
//                      d.is_enabled = true AND
//                      d.criation_date >= ?
//                """;
//    }
//
    public List<DeviceEntity> findDevicesByWorkerNameAndPoolId(String workerName, String poolId) {
        String sql = "SELECT d.* " +
                "FROM devices d " +
                "INNER JOIN workers w ON d.worker_id = w.id " +
                "WHERE w."name" = (:name) " +
                "   AND w.pool_id = (:pool_id)" +
                "   AND d.is_enabled = true";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", workerName);
        parameters.addValue("pool_id", poolId);

        return namedParameterJdbcTemplate.query(sql, parameters, DEVICE_ENTITY_ROW_MAPPER);
    }

    @Override
    public List<DeviceStatisticEntity> getWorkerDevicesStatistic(String poolId, String workerName) {
        findDevicesByWorkerName(workerName)

        List<DeviceSharesStatisticEntity> deviceSharesStatistics = jdbcTemplate.query(
                """ 
                        SELECT s.worker,
                               s.device,
                               max(CASE WHEN s.isvalid = true THEN s.created END)                                    AS last_valid_share_date,
                               CASE WHEN max(s.created) >= NOW() - make_interval(secs => ?) THEN true ELSE false END AS is_online
                        FROM shares_statistic s
                        WHERE s.worker = ?
                        GROUP BY s.device, s.worker""",
                DEVICE_STATISTIC_ROW_MAPPER,
                deviceOnlineCheckInterval,
                workerName);

        //todo: this query execute more then 6 second - need to think how to increase performance
        List<DeviceHashRateStatisticEntity> deviceHashRateStatistics = jdbcTemplate.query("""
                        SELECT split_part(ms.worker, '.', 1) AS worker,
                               split_part(ms.worker, '.', 2) AS device,
                               (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - make_interval(secs => ?)) AS current_hashrate,
                               (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - interval '1 hour')        AS average_hashrate_hour,
                               (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - interval '1 day')         AS average_hashrate_day
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
                deviceOnlineCheckInterval,
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
                "SELECT COUNT(DISTINCT s.worker) FROM shares s WHERE s.poolid = ? AND created >= NOW() - make_interval(secs => ?)",
                Integer.class,
                poolId,
                deviceOnlineCheckInterval);
    }
}
