package com.itrailmpool.itrailmpoolviewer.mapper;

import com.itrailmpool.itrailmpoolviewer.client.model.AggregatedPoolStats;
import com.itrailmpool.itrailmpoolviewer.client.model.Block;
import com.itrailmpool.itrailmpoolviewer.client.model.MinerPerformanceStats;
import com.itrailmpool.itrailmpoolviewer.client.model.MinerStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.client.model.Payment;
import com.itrailmpool.itrailmpoolviewer.client.model.PoolResponse;
import com.itrailmpool.itrailmpoolviewer.client.model.PoolStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.client.model.WorkerPerformanceStatsContainer;
import com.itrailmpool.itrailmpoolviewer.model.AggregatedPoolStatsDto;
import com.itrailmpool.itrailmpoolviewer.model.BlockDto;
import com.itrailmpool.itrailmpoolviewer.model.MinerPerformanceStatsDto;
import com.itrailmpool.itrailmpoolviewer.model.MinerStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.PaymentDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolStatisticContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainerDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MiningcoreClientMapper {

    PoolContainerDto toPoolResponseDto(PoolResponse poolResponse);

    List<WorkerPerformanceStatsContainerDto> toWorkerPerformanceStatsContainerDto(List<WorkerPerformanceStatsContainer> workerPerformanceStatsContainer);

    WorkerPerformanceStatsContainerDto toWorkerPerformanceStatsContainerDto(WorkerPerformanceStatsContainer workerPerformanceStatsContainer);

    MinerStatisticDto toMinerStatisticDto(MinerStatisticResponse minerStatisticResponse);

    List<BlockDto> toBlockDto(List<Block> aggregatedPoolStats);

    BlockDto toBlockDto(Block block);

    List<PaymentDto> toPaymentDto(List<Payment> aggregatedPoolStats);

    PaymentDto toPaymentDto(Payment payment);

    PoolStatisticContainerDto toPoolStatisticContainerDto(PoolStatisticResponse poolStatisticResponse);

    List<AggregatedPoolStatsDto> toAggregatedPoolStatsDto(List<AggregatedPoolStats> aggregatedPoolStats);

    AggregatedPoolStatsDto toAggregatedPoolStatsDto(AggregatedPoolStats aggregatedPoolStats);

    List<MinerPerformanceStatsDto> toMinerPerformanceStatsDto(List<MinerPerformanceStats> aggregatedPoolStats);

    MinerPerformanceStatsDto toMinerPerformanceStatsDto(MinerPerformanceStats minerPerformanceStats);
}
