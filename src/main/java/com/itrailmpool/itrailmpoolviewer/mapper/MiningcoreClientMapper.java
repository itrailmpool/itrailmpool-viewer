package com.itrailmpool.itrailmpoolviewer.mapper;

import com.itrailmpool.itrailmpoolviewer.client.model.MinerStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.client.model.PoolResponse;
import com.itrailmpool.itrailmpoolviewer.client.model.WorkerPerformanceStatsContainer;
import com.itrailmpool.itrailmpoolviewer.model.MinerStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolResponseDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainerDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MiningcoreClientMapper {

    PoolResponseDto toPoolResponseDto(PoolResponse poolResponse);

    List<WorkerPerformanceStatsContainerDto> toWorkerPerformanceStatsContainerDto(List<WorkerPerformanceStatsContainer> workerPerformanceStatsContainer);

    WorkerPerformanceStatsContainerDto toWorkerPerformanceStatsContainerDto(WorkerPerformanceStatsContainer workerPerformanceStatsContainer);

    MinerStatisticDto toMinerStatisticDto(MinerStatisticResponse minerStatisticResponse);
}
