package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.client.MiningcoreClient;
import com.itrailmpool.itrailmpoolviewer.client.model.PoolInfo;
import com.itrailmpool.itrailmpoolviewer.dal.repository.DeviceStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.mapper.PoolResponseMapper;
import com.itrailmpool.itrailmpoolviewer.model.Block;
import com.itrailmpool.itrailmpoolviewer.model.MinerPerformanceStatsDto;
import com.itrailmpool.itrailmpoolviewer.model.MinerStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.model.Payment;
import com.itrailmpool.itrailmpoolviewer.model.PoolResponseDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PoolStatisticServiceImpl implements PoolStatisticService {

    private final MiningcoreClient miningcoreClient;
    private final DeviceStatisticRepository deviceStatisticRepository;
    private final PoolResponseMapper poolResponseMapper;

    @Override
    public PoolResponseDto getPools() {
        var pools = miningcoreClient.getPools();
        pools.getPools().forEach(this::updateConnectedMiners);

        return poolResponseMapper.toPoolResponseDto(pools);
    }

    @Override
    public PoolStatisticResponse getPoolPerformance(String poolId) {
        return miningcoreClient.getPoolPerformance(poolId);
    }

    @Override
    public List<Block> getBlocks(String poolId, int page, int size) {
        return miningcoreClient.getBlocks(poolId, page, size);
    }

    @Override
    public List<Payment> getPayments(String poolId, int page, int size) {
        return miningcoreClient.getPayments(poolId, page, size);
    }

    @Override
    public MinerStatisticResponse getMinerStatistic(String poolId, String address) {
        return miningcoreClient.getMinerStatistic(poolId, address);
    }

    @Override
    public List<WorkerPerformanceStatsContainer> getMinerPerformance(String poolId, String address) {
        return miningcoreClient.getMinerPerformance(poolId, address);
    }

    @Override
    public List<MinerPerformanceStatsDto> getMiners(String poolId, int page, int size) {
        return miningcoreClient.getMiners(poolId, page, size);
    }

    private void updateConnectedMiners(PoolInfo pool) {
        Integer activeWorkersCount = deviceStatisticRepository.getActiveWorkersCount(pool.getId());
        pool.getPoolStats().setConnectedMiners(activeWorkersCount);
    }
}