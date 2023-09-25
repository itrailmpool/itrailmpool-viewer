package com.itrailmpool.itrailmpoolviewer.service.job;

import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerSettingsEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerPaymentStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerShareStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.repository.MinerSettingsRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.WorkerStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.service.WorkerStatisticServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class WorkerStatisticUpdateJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStatisticServiceImpl.class);

    private final MinerSettingsRepository minerSettingsRepository;
    private final WorkerStatisticRepository workerStatisticRepository;
    private final TransactionTemplate transactionTemplate;

    @Scheduled(initialDelay = 5, fixedDelay = 3600, timeUnit = TimeUnit.MINUTES)
    private void saveWorkersDailyStatistic() {
        LOGGER.info("Saving workers daily statistic");

        LocalDate lastWorkerDailyStatisticDate = workerStatisticRepository.getLastWorkerDailyStatisticDate();

        transactionTemplate.executeWithoutResult(status -> {
            minerSettingsRepository.findAll()
                    .forEach(minerSettings -> this.updateWorkerStatisticData(minerSettings, lastWorkerDailyStatisticDate));
        });

        LOGGER.info("Workers daily statistic saved");
    }

    @Scheduled(initialDelay = 30, fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    private void updateWorkerDailyStatistic() {
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
        WorkerStatisticEntity lastWorkerDailyStatistic = workerStatisticRepository.getLastWorkerDailyStatistic(poolId, workerName);
        Instant lastModificationDate = lastWorkerDailyStatistic.getModifiedDate();

        WorkerShareStatisticEntity currentWorkerShareStatistics =
                workerStatisticRepository.getWorkerShareStatisticsFromDate(poolId, workerName, lastModificationDate);
        WorkerHashRateStatisticEntity currentWorkerHashRateStatistic =
                workerStatisticRepository.getWorkerHashRateStatisticFromDate(poolId, workerName, lastModificationDate);
        WorkerPaymentStatisticEntity currentWorkerPaymentStatistic =
                workerStatisticRepository.getWorkerPaymentStatisticFromDate(poolId, workerName, lastModificationDate);

        BigInteger totalAcceptedShares = lastWorkerDailyStatistic.getTotalAcceptedShares().add(currentWorkerShareStatistics.getTotalAcceptedShares());
        BigInteger totalRejectedShares = lastWorkerDailyStatistic.getTotalRejectedShares().add(currentWorkerShareStatistics.getTotalRejectedShares());
        lastWorkerDailyStatistic.setTotalAcceptedShares(totalAcceptedShares);
        lastWorkerDailyStatistic.setTotalRejectedShares(totalRejectedShares);
        lastWorkerDailyStatistic.setModifiedDate(currentWorkerShareStatistics.getDate());
        lastWorkerDailyStatistic.setAverageHashRate(currentWorkerHashRateStatistic.getAverageHashRate());
        lastWorkerDailyStatistic.setTotalPayment(currentWorkerPaymentStatistic.getTotalPayments());

        workerStatisticRepository.update(lastWorkerDailyStatistic);
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
//                .filter(stat -> stat.getDate().isBefore(LocalDate.now()))
                .toList());
    }
}

