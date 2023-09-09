package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.model.BlockDto;
import com.itrailmpool.itrailmpoolviewer.model.MinerPerformanceStatsDto;
import com.itrailmpool.itrailmpoolviewer.model.MinerStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.PaymentDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolStatisticContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainerDto;

import java.util.List;

public interface PoolStatisticService {

    PoolContainerDto getPools();

    PoolStatisticContainerDto getPoolPerformance(String poolId);

    List<BlockDto> getBlocks(String poolId, int page, int size);

    List<PaymentDto> getPayments(String poolId, int page, int size);

    MinerStatisticDto getMinerStatistic(String poolId, String address);

    List<WorkerPerformanceStatsContainerDto> getMinerPerformance(String poolId, String address);

    List<MinerPerformanceStatsDto> getMiners(String poolId, int page, int size);
}
