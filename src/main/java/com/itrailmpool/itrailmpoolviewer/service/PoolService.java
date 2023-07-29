package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.client.MiningcoreClient;
import com.itrailmpool.itrailmpoolviewer.dal.repository.ShareStatisticRepositoryImpl;
import com.itrailmpool.itrailmpoolviewer.model.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PoolService {

    private final MiningcoreClient miningcoreClient;
    private final ShareStatisticRepositoryImpl shareStatisticRepository;

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
}
