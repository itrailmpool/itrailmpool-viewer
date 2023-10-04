package com.itrailmpool.itrailmpoolviewer.service.job;

import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerSettingsEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.repository.MinerSettingsRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.WorkerStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.service.WorkerStatisticServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class WorkerStatisticUpdateJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStatisticServiceImpl.class);

    private final MinerSettingsRepository minerSettingsRepository;
    private final WorkerStatisticRepository workerStatisticRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    private void saveWorkersDailyStatistic() {
        LOGGER.info("Saving workers daily statistic");

        LocalDate lastWorkerDailyStatisticDate = workerStatisticRepository.getLastWorkerDailyStatisticDate();

        minerSettingsRepository.findAll()
                .forEach(minerSettings -> this.updateWorkerStatisticData(minerSettings, lastWorkerDailyStatisticDate));

        LOGGER.info("Workers daily statistic saved");
    }

    private void updateWorkerStatisticData(MinerSettingsEntity minerSettings, LocalDate dateFrom) {
        updateWorkerStatisticData(minerSettings.getPoolId(), minerSettings.getWorkerName(), dateFrom);
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

