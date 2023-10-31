package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerPaymentStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerShareStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface WorkerStatisticRepository {

    List<WorkerStatisticEntity> getWorkerStatistic(String poolId, String workerName);

    List<WorkerStatisticEntity> getWorkerStatisticFromDate(String poolId, String workerName, LocalDate dateFrom);

    List<WorkerStatisticEntity> getWorkerStatisticBetweenDates(String poolId, String workerName, LocalDate dateFrom, LocalDate dateTo);

    WorkerHashRateEntity getWorkerHashRate(String poolId, String workerName);

    LocalDate getLastWorkerDailyStatisticDate();

    void saveAll(List<WorkerStatisticEntity> workerStatisticEntities);

    void update(WorkerStatisticEntity workerStatisticEntities);

    WorkerStatisticEntity getLastWorkerDailyStatistic(String poolId, String workerName);

    List<WorkerShareStatisticEntity> getWorkerShareStatisticsFromDate(String poolId, String workerName, Instant dateFrom);

    List<WorkerHashRateStatisticEntity> getWorkerHashRateStatisticFromDate(String poolId, String workerName, Instant dateFrom);

    List<WorkerPaymentStatisticEntity> getWorkerPaymentStatisticFromDate(String poolId, String workerName, Instant dateFrom);
}
