package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainer;
import com.itrailmpool.itrailmpoolviewer.model.WorkerStatisticContainer;

import java.util.List;

public interface WorkerStatisticService {

    WorkerStatisticContainer getWorkerStatistic(String poolId, String workerName);
    List<WorkerPerformanceStatsContainer> getWorkerPerformance(String poolId, String workerName);
}
