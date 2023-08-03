package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;

import java.util.List;

public interface WorkerStatisticRepository {

    List<WorkerStatisticEntity> getWorkerStatistic(String poolId, String workerName);
    WorkerHashRateEntity getWorkerHashRate(String poolId, String workerName);
}
