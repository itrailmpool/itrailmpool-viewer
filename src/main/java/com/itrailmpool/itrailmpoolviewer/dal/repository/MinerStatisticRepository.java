package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.AverageWorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerPerformanceStatsEntity;

import java.time.Instant;
import java.util.List;

public interface MinerStatisticRepository {

    List<AverageWorkerStatisticEntity> getWorkerAverageHashRateAfterDate(String worker, List<String> workerDevices, Instant dateFrom);

    List<WorkerPerformanceStatsEntity> getWorkerPerformance(String poolId, String address, String workerName);
}
