package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.model.Block;
import com.itrailmpool.itrailmpoolviewer.model.MinerPerformanceStatsDto;
import com.itrailmpool.itrailmpoolviewer.model.MinerStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.Payment;
import com.itrailmpool.itrailmpoolviewer.model.PoolResponseDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainerDto;

import java.util.List;

public interface PoolStatisticService {

    PoolResponseDto getPools();

    PoolStatisticResponse getPoolPerformance(String poolId);

    List<Block> getBlocks(String poolId, int page, int size);

    List<Payment> getPayments(String poolId, int page, int size);

    MinerStatisticDto getMinerStatistic(String poolId, String address);

    List<WorkerPerformanceStatsContainerDto> getMinerPerformance(String poolId, String address);

    List<MinerPerformanceStatsDto> getMiners(String poolId, int page, int size);
}
