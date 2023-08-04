package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.model.Block;
import com.itrailmpool.itrailmpoolviewer.model.MinerPerformanceStatsDto;
import com.itrailmpool.itrailmpoolviewer.model.MinerStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.model.Payment;
import com.itrailmpool.itrailmpoolviewer.model.PoolResponseDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainer;

import java.util.List;

public interface PoolStatisticService {

    PoolResponseDto getPools();

    PoolStatisticResponse getPoolPerformance(String poolId);

    List<Block> getBlocks(String poolId, int page, int size);

    List<Payment> getPayments(String poolId, int page, int size);

    MinerStatisticResponse getMinerStatistic(String poolId, String address);

    List<WorkerPerformanceStatsContainer> getMinerPerformance(String poolId, String address);

    List<MinerPerformanceStatsDto> getMiners(String poolId, int page, int size);
}
