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

    private volatile Map<String, WorkerStatisticContainerDto> workerStatisticByWorker = new HashMap<>();

    @PostConstruct
    void init() {
        reload();
    }

    @Override
    public WorkerStatisticContainerDto getWorkerStatistic(String poolId, String workerName) {
        if (schedulingConfig.isEnable()) {
            LOGGER.debug("Scheduling is enable. Get worker statistic from cache");

            return workerStatisticByWorker.get(buildPoolWorkerKey(poolId, workerName));
        }

        LOGGER.debug("Scheduling is disable. Get worker statistic from database");

        return getWorkerStatistic(poolId, workerName);
    }

    @Override
    public List<WorkerPerformanceStatsContainerDto> getWorkerPerformance(String poolId, String workerName) {
        MinerSettingsEntity minerSettings = minerSettingsRepository.findByPoolIdAndWorkerName(poolId, workerName);
        if (minerSettings == null) {
            return Collections.emptyList();
        }

        List<WorkerPerformanceStatsContainer> minerPerformance = miningcoreClient.getMinerPerformance(poolId, minerSettings.getAddress());

        return miningcoreClientMapper.toWorkerPerformanceStatsContainerDto(minerPerformance);
    }

    @Scheduled(initialDelay = 60, fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    private void reload() {
        LOGGER.info("WorkerStatistic cache reloading");
        workerStatisticByWorker = minerSettingsRepository.findAll().stream()
                .map(this::getWorkerStatisticData)
                .collect(Collectors.toMap(workerStatistic ->
                        buildPoolWorkerKey(workerStatistic.getPoolId(), workerStatistic.getWorkerName()), Function.identity()));
    }

    private WorkerStatisticContainerDto getWorkerStatisticData(MinerSettingsEntity minerSettings) {
        return getWorkerStatisticData(minerSettings.getPoolId(), minerSettings.getWorkerName());
    }

    private WorkerStatisticContainerDto getWorkerStatisticData(String poolId, String workerName) {
        List<DeviceStatisticEntity> devicesStatistic = deviceStatisticRepository.getWorkerDevicesStatistic(poolId, workerName);
        WorkerHashRateEntity workerHashRateEntity = workerStatisticRepository.getWorkerHashRate(poolId, workerName);
        List<WorkerStatisticEntity> workerStatistic  = workerStatisticRepository.getWorkerStatistic(poolId, workerName).stream()
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

    private String buildPoolWorkerKey(String poolId, String workerName) {
        return poolId + KEY_SPLITTER + workerName;
    }
}
