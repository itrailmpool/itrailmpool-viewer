package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceHashRateStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceSharesStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerEntity;
import com.itrailmpool.itrailmpoolviewer.mapper.DeviceStatisticMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.itrailmpool.itrailmpoolviewer.dal.repository.DeviceRepositoryImpl.DEVICE_ENTITY_ROW_MAPPER;
import static com.itrailmpool.itrailmpoolviewer.service.WorkerStatisticServiceImpl.buildPoolWorkerKey;

@Repository
@RequiredArgsConstructor
public class DeviceStatisticRepositoryImpl implements DeviceStatisticRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceStatisticRepositoryImpl.class);
    private static final RowMapper<DeviceSharesStatisticEntity> DEVICE_STATISTIC_ROW_MAPPER = getDeviceStatisticRowMapper();
    private static final RowMapper<DeviceHashRateStatisticEntity> DEVICE_HASH_RATE_STATISTIC_ROW_MAPPER = getDeviceHashRateStatisticRowMapper();

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DeviceStatisticMapper deviceStatisticMapper;
    private final DeviceRepository deviceRepository;
    private final WorkerRepository workerRepository;
    @Value("${app.pool.statistic.device.online.check.interval:600}")
    private Integer deviceOnlineCheckInterval;
    private volatile Map<String, List<String>> devicesNamesByWorker = new HashMap<>();

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

    public List<DeviceEntity> findDevicesFromShareStatistic(String workerName, String poolId, Instant dateFrom) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("worker", workerName);
        parameters.addValue("poolid", poolId);
        parameters.addValue("created", Timestamp.from(dateFrom));

        return namedParameterJdbcTemplate.query("""
                        SELECT CASE WHEN s.device IS NULL THEN '' ELSE s.device END AS name,
                               w.id                                                 AS worker_id,
                               max(CASE WHEN s.isvalid = true THEN s.created END)   AS last_valid_share_date,
                               now()                                                AS creation_date,
                               now()                                                AS modification_date,
                               true                                                 AS is_enabled
                        FROM shares_statistic s
                        INNER JOIN workers w ON s.worker = w.name
                        WHERE s.worker = (:worker)
                             AND s.created >= (:created)
                             AND s.poolid = (:poolid)
                        GROUP BY s.device, w.id""",
                parameters,
                DEVICE_ENTITY_ROW_MAPPER);
    }

    @Scheduled(initialDelay = 1, fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    public void reloadWorkerDevicesNames() {
        LOGGER.info("WorkerDevicesNames cache reloading");
        try {
            Map<String, List<String>> initDevicesNamesByWorkerMap = new HashMap<>();
            Instant dateFrom = Instant.now().minus(1, ChronoUnit.DAYS);
            workerRepository.findAll().forEach(worker ->
                    initDevicesNamesByWorkerMap.put(
                            buildPoolWorkerKey(worker.getPoolId(), worker.getName()),
                            findWorkerDevicesNames(worker.getName(), worker.getPoolId(), dateFrom)));
            devicesNamesByWorker = initDevicesNamesByWorkerMap;
        } catch (Throwable e) {
            LOGGER.error("Unable to reload worker devices names", e);
        }
        LOGGER.info("WorkerDevicesNames cache reloaded");
    }

    private List<String> findWorkerDevicesNames(String workerName, String poolId) {
        if (MapUtils.isNotEmpty(devicesNamesByWorker)) {
            String poolWorkerKey = buildPoolWorkerKey(poolId, workerName);

            return devicesNamesByWorker.get(poolWorkerKey);
        } else {
            Instant dateFrom = Instant.now().minus(1, ChronoUnit.DAYS);

            return findWorkerDevicesNames(workerName, poolId, dateFrom);
        }
    }

    private List<String> findWorkerDevicesNames(String workerName, String poolId, Instant dateFrom) {
        WorkerEntity worker = workerRepository.findByNameAndPoolId(workerName, poolId);

        if (worker == null) {
            return Collections.emptyList();
        }

        List<String> devicesNames = deviceRepository.findByWorkerId(worker.getId()).stream()
                .map(DeviceEntity::getName)
                .toList();
        List<String> devicesFromSharesStatistic = findDevicesFromShareStatistic(workerName, poolId, dateFrom).stream()
                .map(DeviceEntity::getName)
                .toList();

        Set<String> workerDevicesNames = new HashSet<>();
        workerDevicesNames.addAll(devicesNames);
        workerDevicesNames.addAll(devicesFromSharesStatistic);

        LOGGER.info("Worker's {} devices: [{}]", workerName, StringUtils.join(workerDevicesNames, ", "));

        return new ArrayList<>(workerDevicesNames);
    }


    @Override
    public List<DeviceStatisticEntity> getWorkerDevicesStatistic(String poolId, String workerName) {
        List<String> workerDevicesNames = findWorkerDevicesNames(workerName, poolId);
        WorkerEntity worker = workerRepository.findByNameAndPoolId(workerName, poolId);

        if (worker == null || workerDevicesNames.isEmpty()) {
            return Collections.emptyList();
        }

        List<DeviceSharesStatisticEntity> deviceSharesStatistics = getDeviceSharesStatistics(worker, workerDevicesNames);
        List<DeviceHashRateStatisticEntity> deviceHashRateStatistics = getDeviceHashRateStatistics(workerName, poolId, workerDevicesNames);

        return deviceStatisticMapper.toDeviceStatisticEntity(deviceSharesStatistics, deviceHashRateStatistics);
    }

    private List<DeviceSharesStatisticEntity> getDeviceSharesStatistics(WorkerEntity worker, List<String> workerDevicesNames) {
        Map<String, DeviceSharesStatisticEntity> aggregatedDeviceSharesStatistics = deviceRepository.findByWorkerId(worker.getId()).stream()
                .map(workerDevice -> {
                    DeviceSharesStatisticEntity deviceSharesStatistic = new DeviceSharesStatisticEntity();
                    deviceSharesStatistic.setDeviceName(workerDevice.getName());
                    deviceSharesStatistic.setWorkerName(worker.getName());
                    deviceSharesStatistic.setLastValidShareDate(workerDevice.getLastValidShareDate());
                    deviceSharesStatistic.setIsOnline(isDeviceOnline(workerDevice.getLastValidShareDate()));

                    return deviceSharesStatistic;
                })
                .collect(Collectors.toMap(DeviceSharesStatisticEntity::getDeviceName, Function.identity()));
        List<DeviceSharesStatisticEntity> deviceSharesStatistics = getDeviceSharesStatistics(worker.getName(), worker.getPoolId(), workerDevicesNames);

        List<DeviceSharesStatisticEntity> result = new ArrayList<>(aggregatedDeviceSharesStatistics.size());

        deviceSharesStatistics.forEach(deviceSharesStatistic -> {
            if (aggregatedDeviceSharesStatistics.containsKey(deviceSharesStatistic.getDeviceName())) {
                DeviceSharesStatisticEntity device = aggregatedDeviceSharesStatistics.get(deviceSharesStatistic.getDeviceName());
                device.setIsOnline(deviceSharesStatistic.getIsOnline());
                device.setLastValidShareDate(deviceSharesStatistic.getLastValidShareDate());
            } else {
                result.add(deviceSharesStatistic);
            }
        });

        result.addAll(aggregatedDeviceSharesStatistics.values());

        return result;
    }

    private Boolean isDeviceOnline(Instant lastValidShareDate) {
        if (lastValidShareDate == null) {
            return false;
        }

        return lastValidShareDate.isAfter(Instant.now().minus(deviceOnlineCheckInterval, ChronoUnit.SECONDS));
    }


    private List<DeviceSharesStatisticEntity> getDeviceSharesStatistics(String workerName, String poolId, List<String> workerDevicesNames) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("deviceOnlineCheckInterval", deviceOnlineCheckInterval);
        parameters.addValue("workerName", workerName);
        parameters.addValue("workerDevicesNames", workerDevicesNames);

        return namedParameterJdbcTemplate.query(
                """ 
                        SELECT s.worker,
                               s.device,
                               max(CASE WHEN s.isvalid = true THEN s.created END)                                    AS last_valid_share_date,
                               CASE WHEN max(s.created) >= NOW() - make_interval(secs => :deviceOnlineCheckInterval) THEN true ELSE false END AS is_online
                        FROM shares_statistic s
                        WHERE s.worker = :workerName AND s.device IN (:workerDevicesNames)
                        GROUP BY s.device, s.worker""",
                parameters,
                DEVICE_STATISTIC_ROW_MAPPER);
    }

    private List<DeviceHashRateStatisticEntity> getDeviceHashRateStatistics(String workerName, String poolId, List<String> workerDevicesNames) {
        List<String> msWorkerDevices = workerDevicesNames.stream()
                .map(deviceName -> {
                    if (deviceName != null) {
                        return workerName + "." + deviceName;
                    } else {
                        return workerName;
                    }
                })
                .toList();

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("deviceOnlineCheckInterval", deviceOnlineCheckInterval);
        parameters.addValue("workerName", workerName);
        parameters.addValue("poolid", poolId);
        parameters.addValue("msWorkerDevices", msWorkerDevices);

        return namedParameterJdbcTemplate.query("""
                        SELECT split_part(ms.worker, '.', 1) AS worker,
                               split_part(ms.worker, '.', 2) AS device,
                               (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - make_interval(secs => :deviceOnlineCheckInterval)) AS current_hashrate,
                               (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - interval '1 hour')                                 AS average_hashrate_hour,
                               (SELECT avg(hashrate) FROM minerstats WHERE worker = ms.worker AND created >= NOW() - interval '1 day')                                  AS average_hashrate_day
                        FROM minerstats ms
                        WHERE poolid = (:poolid)
                            AND ms.created >= NOW() - interval '1 day'
                            AND ms.worker IN (:msWorkerDevices)
                        GROUP BY ms.worker""",
                parameters,
                DEVICE_HASH_RATE_STATISTIC_ROW_MAPPER);
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
