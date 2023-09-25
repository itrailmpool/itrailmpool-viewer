package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.client.MiningcoreClient;
import com.itrailmpool.itrailmpoolviewer.client.model.WorkerPerformanceStatsContainer;
import com.itrailmpool.itrailmpoolviewer.config.SchedulingConfig;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerSettingsEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.repository.DeviceStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.MinerSettingsRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.WorkerStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.exception.MiningPoolViewerException;
import com.itrailmpool.itrailmpoolviewer.mapper.DeviceStatisticMapper;
import com.itrailmpool.itrailmpoolviewer.mapper.MiningcoreClientMapper;
import com.itrailmpool.itrailmpoolviewer.mapper.WorkerStatisticMapper;
import com.itrailmpool.itrailmpoolviewer.model.WorkerDevicesStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerStatisticContainerDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkerStatisticServiceImpl implements WorkerStatisticService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStatisticServiceImpl.class);
    private static final String KEY_SPLITTER = ".";

    private final MiningcoreClient miningcoreClient;
    private final MinerSettingsRepository minerSettingsRepository;
    private final DeviceStatisticRepository deviceStatisticRepository;
    private final WorkerStatisticRepository workerStatisticRepository;
    private final WorkerStatisticMapper workerStatisticMapper;
    private final DeviceStatisticMapper deviceStatisticMapper;
    private final MiningcoreClientMapper miningcoreClientMapper;
    private final SchedulingConfig schedulingConfig;
    private final TransactionTemplate transactionTemplate;

    private volatile Map<String, WorkerStatisticContainerDto> workerStatisticByWorker = new HashMap<>();

    @PostConstruct
    void init() {
        reload();
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
    public List<WorkerPerformanceStatsContainerDto> getWorkerPerformance(String poolId, String workerName) {
        try {
            MinerSettingsEntity minerSettings = minerSettingsRepository.findByPoolIdAndWorkerName(poolId, workerName);
            if (minerSettings == null) {
                return Collections.emptyList();
            }

            List<WorkerPerformanceStatsContainer> minerPerformance = miningcoreClient.getMinerPerformance(poolId, minerSettings.getAddress());

            return miningcoreClientMapper.toWorkerPerformanceStatsContainerDto(minerPerformance);
        } catch (Throwable t) {
            throw new MiningPoolViewerException(t);
        }
    }

    @Scheduled(initialDelay = 10, fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
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
//        List<DeviceStatisticEntity> devicesStatistic = deviceStatisticRepository.getWorkerDevicesStatistic(poolId, workerName);
        List<DeviceStatisticEntity> devicesStatistic = Collections.emptyList();
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
                        .setWorkerDevicesStatistic(deviceStatisticMapper.toDeviceStatistic(devicesStatistic)));
    }

    public static String buildPoolWorkerKey(String poolId, String workerName) {
        return poolId + KEY_SPLITTER + workerName;
    }
}
