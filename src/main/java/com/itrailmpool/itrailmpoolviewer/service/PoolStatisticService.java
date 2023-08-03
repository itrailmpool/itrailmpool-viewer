package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.client.MiningcoreClient;
import com.itrailmpool.itrailmpoolviewer.client.model.PoolInfo;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerSettingsEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.repository.DeviceStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.MinerSettingsRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.WorkerStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.mapper.DeviceStatisticMapper;
import com.itrailmpool.itrailmpoolviewer.mapper.PoolResponseMapper;
import com.itrailmpool.itrailmpoolviewer.mapper.WorkerStatisticMapper;
import com.itrailmpool.itrailmpoolviewer.model.response.Block;
import com.itrailmpool.itrailmpoolviewer.model.response.MinerPerformanceStatsDto;
import com.itrailmpool.itrailmpoolviewer.model.response.MinerStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.model.response.Payment;
import com.itrailmpool.itrailmpoolviewer.model.response.PoolResponseDto;
import com.itrailmpool.itrailmpoolviewer.model.response.PoolStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.model.response.WorkerDevicesStatistic;
import com.itrailmpool.itrailmpoolviewer.model.response.WorkerPerformanceStatsContainer;
import com.itrailmpool.itrailmpoolviewer.model.response.WorkerStatisticResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PoolStatisticService {

    private final MiningcoreClient miningcoreClient;
    private final DeviceStatisticRepository deviceStatisticRepository;
    private final MinerSettingsRepository minerSettingsRepository;
    private final WorkerStatisticRepository workerStatisticRepository;
    private final WorkerStatisticMapper workerStatisticMapper;
    private final DeviceStatisticMapper deviceStatisticMapper;
    private final PoolResponseMapper poolResponseMapper;

    public PoolResponseDto getPools() {
        var pools = miningcoreClient.getPools();
        pools.getPools().forEach(this::updateConnectedMiners);

        return poolResponseMapper.toPoolResponseDto(pools);
    }

    public PoolStatisticResponse getPoolPerformance(String poolId) {
        return miningcoreClient.getPoolPerformance(poolId);
    }

    public List<Block> getBlocks(String poolId, int page, int size) {
        return miningcoreClient.getBlocks(poolId, page, size);
    }

    public List<Payment> getPayments(String poolId, int page, int size) {
        return miningcoreClient.getPayments(poolId, page, size);
    }

    public MinerStatisticResponse getMinerStatistic(String poolId, String address) {
        return miningcoreClient.getMinerStatistic(poolId, address);
    }

    public List<WorkerPerformanceStatsContainer> getMinerPerformance(String poolId, String address) {
        return miningcoreClient.getMinerPerformance(poolId, address);
    }

    public List<MinerPerformanceStatsDto> getMiners(String poolId, int page, int size) {
        return miningcoreClient.getMiners(poolId, page, size);
    }

    private void updateConnectedMiners(PoolInfo pool) {
        Integer activeWorkersCount = deviceStatisticRepository.getActiveWorkersCount(pool.getId());
        pool.getPoolStats().setConnectedMiners(activeWorkersCount);
    }

    public WorkerStatisticResponse getWorkerStatistic(String poolId, String workerName) {
        List<DeviceStatisticEntity> devicesStatistic = deviceStatisticRepository.getWorkerDevicesStatistic(poolId, workerName);
        WorkerHashRateEntity workerHashRateEntity = workerStatisticRepository.getWorkerHashRate(poolId, workerName);
        List<WorkerStatisticEntity> workerStatistic = workerStatisticRepository.getWorkerStatistic(poolId, workerName).stream()
                .sorted(Comparator.comparing(WorkerStatisticEntity::getDate).reversed())
                .toList();

        long totalDevicesCount = devicesStatistic.size();
        long devicesOnline = devicesStatistic.stream().filter(DeviceStatisticEntity::getIsOnline).count();
        long devicesOffline = totalDevicesCount - devicesOnline;

        return new WorkerStatisticResponse()
                .setWorkerHashRate(workerStatisticMapper.toWorkerHashRateDto(workerHashRateEntity))
                .setWorkerStatistics(workerStatisticMapper.toWorkerStatisticDto(workerStatistic))
                .setWorkerDevicesStatistic(new WorkerDevicesStatistic()
                        .setWorkerName(workerName)
                        .setTotalDevices(totalDevicesCount)
                        .setDevicesOnline(devicesOnline)
                        .setDevicesOffline(devicesOffline)
                        .setWorkerDevicesStatistic(deviceStatisticMapper.toDeviceStatistic(devicesStatistic)));
    }

    public List<WorkerPerformanceStatsContainer> getWorkerPerformance(String poolId, String workerName) {
        MinerSettingsEntity minerSettings = minerSettingsRepository.findByPoolIdAndWorkerName(poolId, workerName);
        if (minerSettings == null) {
            return Collections.emptyList();
        }
        return miningcoreClient.getMinerPerformance(poolId, minerSettings.getAddress());
    }
}
