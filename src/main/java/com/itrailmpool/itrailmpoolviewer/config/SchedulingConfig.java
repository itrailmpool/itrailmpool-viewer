package com.itrailmpool.itrailmpoolviewer.config;

import com.itrailmpool.itrailmpoolviewer.dal.repository.MinerSettingsRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.WorkerStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.service.job.WorkerStatisticUpdateJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@PropertySource(value = "classpath:${env:dev}/application.properties")
@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Value("${app.pool.statistic.cache.enabled}")
    private Boolean cacheEnabled;

    public Boolean isCacheEnabled() {
        return cacheEnabled;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.pool.statistic.worker.daily-aggregation", name = "enabled", havingValue = "true")
    public WorkerStatisticUpdateJob workerStatisticUpdateJob(MinerSettingsRepository minerSettingsRepository,
                                                             WorkerStatisticRepository workerStatisticRepository) {
        return new WorkerStatisticUpdateJob(minerSettingsRepository, workerStatisticRepository);
    }
}
