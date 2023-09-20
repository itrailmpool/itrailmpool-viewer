package com.itrailmpool.itrailmpoolviewer.service.job;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class WorkerDevicesUpdateJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerDevicesUpdateJob.class);


    @Scheduled(cron = "0 0 1 * * ?")
    private void saveWorkerDevices() {
        LOGGER.info("Worker's new devices saving");


        LOGGER.info("Worker's new devices saved");
    }
}
