package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.client.MiningcoreClient;
import com.itrailmpool.itrailmpoolviewer.client.model.Block;
import com.itrailmpool.itrailmpoolviewer.client.model.MinerPerformanceStats;
import com.itrailmpool.itrailmpoolviewer.client.model.MinerStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.client.model.Payment;
import com.itrailmpool.itrailmpoolviewer.client.model.PoolInfo;
import com.itrailmpool.itrailmpoolviewer.client.model.PoolStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.client.model.WorkerPerformanceStatsContainer;
import com.itrailmpool.itrailmpoolviewer.dal.repository.DeviceStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.mapper.MiningcoreClientMapper;
import com.itrailmpool.itrailmpoolviewer.model.BlockDto;
import com.itrailmpool.itrailmpoolviewer.model.MinerPerformanceStatsDto;
import com.itrailmpool.itrailmpoolviewer.model.MinerStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.PaymentDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolStatisticContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PoolStatisticServiceImpl implements PoolStatisticService {

    private final MiningcoreClient miningcoreClient;
    private final DeviceStatisticRepository deviceStatisticRepository;
    private final MiningcoreClientMapper miningcoreClientMapper;

    @Override
    public PoolContainerDto getPools() {
        var pools = miningcoreClient.getPools();
        pools.getPools().forEach(this::updateConnectedMiners);

        return miningcoreClientMapper.toPoolResponseDto(pools);
    }

    @Override
    public PoolStatisticContainerDto getPoolPerformance(String poolId) {
        PoolStatisticResponse poolPerformance = miningcoreClient.getPoolPerformance(poolId);

        return miningcoreClientMapper.toPoolStatisticContainerDto(poolPerformance);
    }

    @Override
    public List<BlockDto> getBlocks(String poolId, int page, int size) {
        List<Block> blocks = miningcoreClient.getBlocks(poolId, page, size);

        return miningcoreClientMapper.toBlockDto(blocks);
    }

    @Override
    public List<PaymentDto> getPayments(String poolId, int page, int size) {
        List<Payment> payments = miningcoreClient.getPayments(poolId, page, size);

        return miningcoreClientMapper.toPaymentDto(payments);
    }

    @Override
    public MinerStatisticDto getMinerStatistic(String poolId, String address) {
        MinerStatisticResponse minerStatistic = miningcoreClient.getMinerStatistic(poolId, address);

        return miningcoreClientMapper.toMinerStatisticDto(minerStatistic);
    }

    @Override
    public List<WorkerPerformanceStatsContainerDto> getMinerPerformance(String poolId, String address) {
        List<WorkerPerformanceStatsContainer> minerPerformance = miningcoreClient.getMinerPerformance(poolId, address);

        return miningcoreClientMapper.toWorkerPerformanceStatsContainerDto(minerPerformance);
    }

    @Override
    public List<MinerPerformanceStatsDto> getMiners(String poolId, int page, int size) {
        List<MinerPerformanceStats> miners = miningcoreClient.getMiners(poolId, page, size);

        return miningcoreClientMapper.toMinerPerformanceStatsDto(miners);
    }

    private void updateConnectedMiners(PoolInfo pool) {
        Integer activeWorkersCount = deviceStatisticRepository.getActiveWorkersCount(pool.getId());
        pool.getPoolStats().setConnectedMiners(activeWorkersCount);
    }
}
