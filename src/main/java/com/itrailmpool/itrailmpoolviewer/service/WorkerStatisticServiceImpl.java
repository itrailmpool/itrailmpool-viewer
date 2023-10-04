package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.client.MiningcoreClient;
import com.itrailmpool.itrailmpoolviewer.client.model.WorkerPerformanceStatsContainer;
import com.itrailmpool.itrailmpoolviewer.config.SchedulingConfig;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerSettingsEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerPerformanceStatsEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.repository.DeviceStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.MinerSettingsRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.MinerStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.WorkerStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.exception.MiningPoolViewerException;
import com.itrailmpool.itrailmpoolviewer.mapper.DeviceStatisticMapper;
import com.itrailmpool.itrailmpoolviewer.mapper.MiningcoreClientMapper;
import com.itrailmpool.itrailmpoolviewer.mapper.WorkerStatisticMapper;
import com.itrailmpool.itrailmpoolviewer.model.DeviceStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerCurrentStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerDevicesStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerStatisticContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerStatisticDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

@Service
@RequiredArgsConstructor
public class WorkerStatisticServiceImpl implements WorkerStatisticService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStatisticServiceImpl.class);
    private static final String KEY_SPLITTER = ".";
    private static final Comparator<DeviceStatisticDto> DEFAULT_DEVICE_STATISTIC_COMPARATOR = Comparator.comparing(DeviceStatisticDto::getLastShareDate);

    private final MiningcoreClient miningcoreClient;
    private final MinerSettingsRepository minerSettingsRepository;
    private final DeviceStatisticRepository deviceStatisticRepository;
    private final WorkerStatisticRepository workerStatisticRepository;
    private final MinerStatisticRepository minerStatisticRepository;
    private final WorkerStatisticMapper workerStatisticMapper;
    private final DeviceStatisticMapper deviceStatisticMapper;
    private final MiningcoreClientMapper miningcoreClientMapper;
    private final SchedulingConfig schedulingConfig;
    private final TransactionTemplate transactionTemplate;

    private volatile Map<String, WorkerStatisticContainerDto> workerStatisticByWorker = new HashMap<>();

    private final Map<String, Comparator<DeviceStatisticDto>> deviceStatisticComparators = Map.of(
            "devicename", Comparator.comparing(DeviceStatisticDto::getDeviceName),
            "lastsharedate", Comparator.comparing(DeviceStatisticDto::getLastShareDate),
            "currenthashrate", Comparator.comparing(DeviceStatisticDto::getCurrentHashRate),
            "hourlyaveragehashrate", Comparator.comparing(DeviceStatisticDto::getHourlyAverageHashRate),
            "dailyaveragehashrate", Comparator.comparing(DeviceStatisticDto::getDailyAverageHashRate),
            "isonline", Comparator.comparing(DeviceStatisticDto::getIsOnline, Comparator.nullsLast(Comparator.naturalOrder()))
    );

    @PostConstruct
    void init() {
        reload();
    }

    public static String buildPoolWorkerKey(String poolId, String workerName) {
        return poolId + KEY_SPLITTER + workerName;
    }

    @Override
    public WorkerStatisticContainerDto getWorkerStatistic(String poolId, String workerName) {
        try {
            if (schedulingConfig.isCacheEnabled()) {
                LOGGER.debug("Scheduling is enable. Get worker statistic from cache");

                return workerStatisticByWorker.get(buildPoolWorkerKey(poolId, workerName));
            }

            LOGGER.debug("Scheduling is disable. Get worker statistic from database");

            return getWorkerStatisticData(poolId, workerName);
        } catch (Throwable t) {
            throw new MiningPoolViewerException(t);
        }
    }

    @Override
    public WorkerCurrentStatisticDto getWorkerCurrentStatistic(String poolId, String workerName) {
        WorkerStatisticContainerDto workerStatisticContainer = workerStatisticByWorker.get(buildPoolWorkerKey(poolId, workerName));

        if (workerStatisticContainer == null) {
            return new WorkerCurrentStatisticDto();
        }

        return new WorkerCurrentStatisticDto()
                .setWorkerName(workerName)
                .setCurrentHashRate(workerStatisticContainer.getWorkerHashRate().getCurrentHashRate())
                .setHourlyAverageHashRate(workerStatisticContainer.getWorkerHashRate().getHourlyAverageHashRate())
                .setDailyAverageHashRate(workerStatisticContainer.getWorkerHashRate().getDailyAverageHashRate())
                .setTotalDevices(workerStatisticContainer.getWorkerDevicesStatistic().getTotalDevices())
                .setDevicesOnline(workerStatisticContainer.getWorkerDevicesStatistic().getDevicesOnline())
                .setDevicesOffline(workerStatisticContainer.getWorkerDevicesStatistic().getDevicesOffline());
    }

    @Override
    public List<WorkerPerformanceStatsContainerDto> getWorkerPerformance(String poolId, String workerName) {
        try {
            MinerSettingsEntity minerSettings = minerSettingsRepository.findByPoolIdAndWorkerName(poolId, workerName);
            if (minerSettings == null) {
                return Collections.emptyList();
            }

            List<WorkerPerformanceStatsEntity> workerPerformance = minerStatisticRepository.getWorkerPerformance(poolId, minerSettings.getAddress(), workerName);
            List<WorkerPerformanceStatsContainer> workerPerformanceStatsContainers = groupWorkerPerformanceStats(workerPerformance);

            return miningcoreClientMapper.toWorkerPerformanceStatsContainerDto(workerPerformanceStatsContainers);
        } catch (Throwable t) {
            throw new MiningPoolViewerException(t);
        }
    }

    public List<WorkerPerformanceStatsContainer> groupWorkerPerformanceStats(List<WorkerPerformanceStatsEntity> entities) {
        Map<Instant, Map<String, WorkerPerformanceStatsEntity>> grouped = entities.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCreated().truncatedTo(ChronoUnit.HOURS),
                        Collectors.toMap(
                                WorkerPerformanceStatsEntity::getWorkerDeviceKey,
                                e -> {
                                    WorkerPerformanceStatsEntity stats = new WorkerPerformanceStatsEntity();
                                    stats.setHashRate(e.getHashRate());
                                    stats.setSharesPerSecond(e.getSharesPerSecond());
                                    return stats;
                                },
                                (s1, s2) -> {
                                    WorkerPerformanceStatsEntity merged = new WorkerPerformanceStatsEntity();
                                    merged.setHashRate(s1.getHashRate().add(s2.getHashRate()).divide(BigDecimal.valueOf(2)));
                                    merged.setSharesPerSecond(s1.getSharesPerSecond().add(s2.getSharesPerSecond()).divide(BigDecimal.valueOf(2)));
                                    return merged;
                                }
                        )
                ));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    WorkerPerformanceStatsContainer container = new WorkerPerformanceStatsContainer();
                    container.setCreated(entry.getKey());
                    container.setWorkers(entry.getValue());
                    return container;
                })
                .toList();
    }

    @Override
    public Page<WorkerStatisticDto> getWorkerStatistic(Pageable pageable, String poolId, String workerName) {
        WorkerStatisticContainerDto workerStatisticContainer = workerStatisticByWorker.get(buildPoolWorkerKey(poolId, workerName));

        if (workerStatisticContainer == null) {
            return Page.empty();
        }

        List<WorkerStatisticDto> workerStatistics = workerStatisticContainer.getWorkerStatistics();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), workerStatistics.size());
        return new PageImpl<>(workerStatistics.subList(start, end), pageable, workerStatistics.size());
    }

    @Override
    public Page<DeviceStatisticDto> getDeviceStatistics(Pageable pageable, String poolId, String workerName, String deviceName) {
        WorkerStatisticContainerDto workerStatisticContainer = workerStatisticByWorker.get(buildPoolWorkerKey(poolId, workerName));

        if (workerStatisticContainer == null) {
            return Page.empty();
        }

        List<DeviceStatisticDto> devicesStatistics = workerStatisticContainer.getWorkerDevicesStatistic().getWorkerDevicesStatistic();

        if (isNotEmpty(deviceName)) {
            devicesStatistics = devicesStatistics.stream()
                    .filter(device -> startsWithIgnoreCase(device.getDeviceName(), deviceName))
                    .toList();
        }

        if (pageable.getSort().isSorted()) {
            Comparator<DeviceStatisticDto> comparator = createComparator(pageable.getSort().toList());
            devicesStatistics = devicesStatistics.stream()
                    .sorted(comparator)
                    .toList();
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), devicesStatistics.size());
        return new PageImpl<>(devicesStatistics.subList(start, end), pageable, devicesStatistics.size());
    }

    @Scheduled(initialDelay = 5, fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    private void reload() {
        try {
            LOGGER.info("WorkerStatistic cache reloading");

            transactionTemplate.executeWithoutResult(status -> {
                workerStatisticByWorker = minerSettingsRepository.findAll().stream()
                        .map(this::getWorkerStatisticData)
                        .collect(Collectors.toMap(workerStatistic ->
                                buildPoolWorkerKey(workerStatistic.getPoolId(), workerStatistic.getWorkerName()), Function.identity()));
            });

            LOGGER.info("WorkerStatistic cache reloaded");
        } catch (Throwable t) {
            LOGGER.error("Unable reload WorkerStatistic cache", t);
        }
    }

    private WorkerStatisticContainerDto getWorkerStatisticData(MinerSettingsEntity minerSettings) {
        LOGGER.info("Worker {} statistic cache reloading", minerSettings.getWorkerName());
        WorkerStatisticContainerDto workerStatisticData = getWorkerStatisticData(minerSettings.getPoolId(), minerSettings.getWorkerName());
        LOGGER.info("Worker {} statistic cache reloaded", minerSettings.getWorkerName());

        return workerStatisticData;
    }

    private WorkerStatisticContainerDto getWorkerStatisticData(String poolId, String workerName) {
        List<DeviceStatisticEntity> devicesStatistic = deviceStatisticRepository.getWorkerDevicesStatistic(poolId, workerName);
        WorkerHashRateEntity workerHashRateEntity = workerStatisticRepository.getWorkerHashRate(poolId, workerName);

        List<WorkerStatisticEntity> workerStatistic = workerStatisticRepository.getWorkerStatistic(poolId, workerName).stream()
                .sorted(Comparator.comparing(WorkerStatisticEntity::getDate).reversed())
                .toList();

        long totalDevicesCount = devicesStatistic.size();
        long devicesOnline = devicesStatistic.stream().filter(DeviceStatisticEntity::getIsOnline).count();
        long devicesOffline = totalDevicesCount - devicesOnline;

        return new WorkerStatisticContainerDto()
                .setPoolId(poolId)
                .setWorkerName(workerName)
                .setWorkerHashRate(workerStatisticMapper.toWorkerHashRateDto(workerHashRateEntity))
                .setWorkerStatistics(workerStatisticMapper.toWorkerStatisticDto(workerStatistic))
                .setWorkerDevicesStatistic(new WorkerDevicesStatisticDto()
                        .setWorkerName(workerName)
                        .setTotalDevices(totalDevicesCount)
                        .setDevicesOnline(devicesOnline)
                        .setDevicesOffline(devicesOffline)
                        .setWorkerDevicesStatistic(deviceStatisticMapper.toDeviceStatistic(devicesStatistic)));//todo: remove empty list after fix performance issue
    }

    private Comparator<DeviceStatisticDto> createComparator(List<Sort.Order> orders) {
        Comparator<DeviceStatisticDto> comparator = null;
        for (Sort.Order order : orders) {
            Comparator<DeviceStatisticDto> currentComparator = deviceStatisticComparators.getOrDefault(order.getProperty().toLowerCase(), DEFAULT_DEVICE_STATISTIC_COMPARATOR);
            if (comparator == null) {
                comparator = order.isAscending() ? currentComparator : currentComparator.reversed();
            } else {
                comparator = order.isAscending() ? comparator.thenComparing(currentComparator) : comparator.thenComparing(currentComparator).reversed();
            }
        }
        return comparator;
    }

}
