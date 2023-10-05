package com.itrailmpool.itrailmpoolviewer.service.job;

import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerSettingsEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerPaymentStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerShareStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.repository.MinerSettingsRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.WorkerStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.mapper.WorkerStatisticMapper;
import com.itrailmpool.itrailmpoolviewer.service.WorkerStatisticServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class WorkerStatisticUpdateJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStatisticServiceImpl.class);

    private final MinerSettingsRepository minerSettingsRepository;
    private final WorkerStatisticRepository workerStatisticRepository;
    private final TransactionTemplate transactionTemplate;
    private final WorkerStatisticMapper workerStatisticMapper;

    @Value("${app.pool.statistic.worker.statistic.run-worker-daily-stat-initialize:false}")
    private boolean shouldRunWorkerDailyStatisticInitialization;
    private boolean isWorkerDailyStatisticInitialized = false;

    @Scheduled(initialDelay = 1, fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    private void updateWorkerDailyStatistic() {
        if (shouldRunWorkerDailyStatisticInitialization && !isWorkerDailyStatisticInitialized) {
            saveWorkersDailyStatistic();
            isWorkerDailyStatisticInitialized = true;
        }

        LOGGER.info("Workers daily statistic updating");

        transactionTemplate.executeWithoutResult(status -> {
            minerSettingsRepository.findAll()
                    .forEach(this::updateWorkerDailyStatistic);
        });

        LOGGER.info("Workers daily statistic updated");
    }

    private void updateWorkerDailyStatistic(MinerSettingsEntity minerSettings) {
        try {
            updateWorkerDailyStatistic(minerSettings.getPoolId(), minerSettings.getWorkerName());
        } catch (Throwable e) {
            LOGGER.error("Unable to update worker statistic for pool [{}] worker [{}]: {}",
                    minerSettings.getPoolId(), minerSettings.getWorkerName(), e.getMessage(), e);
        }
    }

    private void updateWorkerDailyStatistic(String poolId, String workerName) {
        WorkerStatisticEntity lastSavedWorkerDailyStatistic = workerStatisticRepository.getLastWorkerDailyStatistic(poolId, workerName);
        Instant lastModificationDate = lastSavedWorkerDailyStatistic == null ?
                Instant.now().minus(1, ChronoUnit.HOURS) :
                lastSavedWorkerDailyStatistic.getModifiedDate();

        List<WorkerShareStatisticEntity> workerShareStatistics =
                workerStatisticRepository.getWorkerShareStatisticsFromDate(poolId, workerName, lastModificationDate);
        List<WorkerHashRateStatisticEntity> workerHashRateStatistic =
                workerStatisticRepository.getWorkerHashRateStatisticFromDate(poolId, workerName, lastModificationDate);
        List<WorkerPaymentStatisticEntity> workerPaymentStatistic =
                workerStatisticRepository.getWorkerPaymentStatisticFromDate(poolId, workerName, lastModificationDate);

        //TODO: remove this hotfix after issue resolving (row mapper)
        workerShareStatistics.forEach(shareStatistics -> {
            LOGGER.debug("shareStatistics {}", shareStatistics);

        });
        workerHashRateStatistic.forEach(hashRateStatistic -> {
            hashRateStatistic.setWorkerName(workerName);
            LOGGER.debug("hashRateStatistic {}", hashRateStatistic);
        });
        workerPaymentStatistic.forEach(paymentStatistic -> {
            paymentStatistic.setWorkerName(workerName);
            LOGGER.debug("paymentStatistic {}", paymentStatistic);
        });

        List<WorkerStatisticEntity> newWorkerStatisticEntities = new ArrayList<>();

        workerStatisticMapper.toWorkerStatistic(workerHashRateStatistic, workerShareStatistics, workerPaymentStatistic).forEach(workerStatisticEntity -> {
            if (lastSavedWorkerDailyStatistic.getPoolId().equals(workerStatisticEntity.getPoolId())
                    && lastSavedWorkerDailyStatistic.getWorkerName().equals(workerStatisticEntity.getWorkerName())
                    && lastSavedWorkerDailyStatistic.getDate().isEqual(workerStatisticEntity.getDate())) {

                BigInteger totalAcceptedShares = lastSavedWorkerDailyStatistic.getTotalAcceptedShares().add(workerStatisticEntity.getTotalAcceptedShares());
                BigInteger totalRejectedShares = lastSavedWorkerDailyStatistic.getTotalRejectedShares().add(workerStatisticEntity.getTotalRejectedShares());
                lastSavedWorkerDailyStatistic.setTotalAcceptedShares(totalAcceptedShares);
                lastSavedWorkerDailyStatistic.setTotalRejectedShares(totalRejectedShares);
                lastSavedWorkerDailyStatistic.setModifiedDate(workerStatisticEntity.getModifiedDate());
                lastSavedWorkerDailyStatistic.setAverageHashRate(workerStatisticEntity.getAverageHashRate());
                lastSavedWorkerDailyStatistic.setTotalPayment(workerStatisticEntity.getTotalPayment());

                LOGGER.debug("workerStatisticEntity for update {}", workerStatisticEntity);

                workerStatisticRepository.update(lastSavedWorkerDailyStatistic);
            } else {
                LOGGER.debug("{}", workerStatisticEntity);
                newWorkerStatisticEntities.add(workerStatisticEntity);
            }
        });

        workerStatisticRepository.saveAll(newWorkerStatisticEntities);
    }

    private void saveWorkersDailyStatistic() {
        LocalDate lastWorkerDailyStatisticDate = workerStatisticRepository.getLastWorkerDailyStatisticDate();
        LOGGER.info("Saving workers daily statistic from date: [{}]", lastWorkerDailyStatisticDate);

        transactionTemplate.executeWithoutResult(status -> {
            minerSettingsRepository.findAll()
                    .forEach(minerSettings -> this.updateWorkerStatisticData(minerSettings, lastWorkerDailyStatisticDate));
        });

        LOGGER.info("Workers daily statistic saved");
    }

    private void updateWorkerStatisticData(MinerSettingsEntity minerSettings, LocalDate dateFrom) {
        try {
            updateWorkerStatisticData(minerSettings.getPoolId(), minerSettings.getWorkerName(), dateFrom);
        } catch (Throwable e) {
            LOGGER.error("Unable to update worker statistic: {}", e.getMessage(), e);
        }
    }

    private void updateWorkerStatisticData(String poolId, String workerName, LocalDate dateFrom) {
        List<WorkerStatisticEntity> workerStatistic;
        if (dateFrom == null) {
            workerStatistic = workerStatisticRepository.getWorkerStatistic(poolId, workerName);
        } else {
            workerStatistic = workerStatisticRepository.getWorkerStatisticFromDate(poolId, workerName, dateFrom);
        }

        workerStatisticRepository.saveAll(workerStatistic.stream()
                .filter(stat -> stat.getDate().isBefore(LocalDate.now()))
                .toList());
    }
}

