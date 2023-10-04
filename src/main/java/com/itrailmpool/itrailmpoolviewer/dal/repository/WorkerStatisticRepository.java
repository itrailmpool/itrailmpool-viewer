package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;

import java.time.LocalDate;
import java.util.List;

public interface WorkerStatisticRepository {

    List<WorkerStatisticEntity> getWorkerStatistic(String poolId, String workerName);
    List<WorkerStatisticEntity> getWorkerStatisticFromDate(String poolId, String workerName, LocalDate dateFrom);
    WorkerHashRateEntity getWorkerHashRate(String poolId, String workerName);
    LocalDate getLastWorkerDailyStatisticDate();
    void saveAll(List<WorkerStatisticEntity> workerStatisticEntities);
}
