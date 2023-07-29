package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.client.MiningcoreClient;
import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerSettings;
import com.itrailmpool.itrailmpoolviewer.dal.repository.MinerSettingsRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.ShareStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.model.response.Block;
import com.itrailmpool.itrailmpoolviewer.model.response.MinerPerformanceStats;
import com.itrailmpool.itrailmpoolviewer.model.response.MinerStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.model.response.Payment;
import com.itrailmpool.itrailmpoolviewer.model.response.PoolInfo;
import com.itrailmpool.itrailmpoolviewer.model.response.PoolResponse;
import com.itrailmpool.itrailmpoolviewer.model.response.PoolStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.model.response.WorkerDevicesStatistic;
import com.itrailmpool.itrailmpoolviewer.model.response.WorkerPerformanceStatsContainer;
import com.itrailmpool.itrailmpoolviewer.model.response.WorkerStatisticResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PoolService {

    private final MiningcoreClient miningcoreClient;
    private final ShareStatisticRepository shareStatisticRepository;
    private final MinerSettingsRepository minerSettingsRepository;

    public PoolResponse getPools() {
        var pools = miningcoreClient.getPools();
        pools.getPools().forEach(this::updateConnectedMiners);

        return pools;
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

    public List<MinerPerformanceStats> getMiners(String poolId, int page, int size) {
        return miningcoreClient.getMiners(poolId, page, size);
    }

    private void updateConnectedMiners(PoolInfo pool) {
        Integer activeWorkersCount = shareStatisticRepository.getActiveWorkersCount(pool.getId());
        pool.getPoolStats().setConnectedMiners(activeWorkersCount);
    }

    public WorkerStatisticResponse getWorkerStatistic(String poolId, String workerName) {
        //todo: implement
        return new WorkerStatisticResponse()
                .setWorkerStatistics(Collections.emptyList())
                .setWorkerDevicesStatistic(new WorkerDevicesStatistic()
                .setTotalDevices(0)
                .setWorkerName("")
                .setDevicesOffline(0)
                .setDevicesOnline(0)
                .setWorkerDevicesStatistic(Collections.emptyList()));
    }

    public List<WorkerPerformanceStatsContainer> getWorkerPerformance(String poolId, String workerName) {
        MinerSettings minerSettings = minerSettingsRepository.findByPoolIdAndWorkerName(poolId, workerName);
        if (minerSettings == null) {
            return Collections.emptyList();
        }
        return miningcoreClient.getMinerPerformance(poolId, minerSettings.getAddress());
    }
}
