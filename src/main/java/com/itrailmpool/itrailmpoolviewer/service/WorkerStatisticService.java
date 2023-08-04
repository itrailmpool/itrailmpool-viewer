package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerStatisticContainerDto;

import java.util.List;

public interface WorkerStatisticService {

    WorkerStatisticContainerDto getWorkerStatistic(String poolId, String workerName);
    List<WorkerPerformanceStatsContainerDto> getWorkerPerformance(String poolId, String workerName);
}
