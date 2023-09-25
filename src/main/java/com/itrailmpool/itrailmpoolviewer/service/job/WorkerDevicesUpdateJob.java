package com.itrailmpool.itrailmpoolviewer.service.job;

import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerEntity;
import com.itrailmpool.itrailmpoolviewer.dal.repository.DeviceRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.DeviceStatisticRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.DEFAULT_DATA_FORMAT_PATTERN;

@Service
@RequiredArgsConstructor
public class WorkerDevicesUpdateJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerDevicesUpdateJob.class);

    private final WorkerRepository workerRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceStatisticRepository deviceStatisticRepository;
    private final TransactionTemplate transactionTemplate;

    @Scheduled(initialDelay = 5, fixedDelay = 20, timeUnit = TimeUnit.MINUTES)
    private void saveWorkerDevicesInit() {
        LOGGER.info("Worker's new devices saving");

        transactionTemplate.executeWithoutResult(status -> workerRepository.findAll()
                .forEach(this::updateDevicesData));

        LOGGER.info("Worker's new devices saved");
    }

    private void updateDevicesData(WorkerEntity worker) {
        try {
            Instant fromDate = Instant.now().minus(60, ChronoUnit.MINUTES);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATA_FORMAT_PATTERN);
            ZonedDateTime zdt = fromDate.atZone(ZoneId.systemDefault());

            LOGGER.info("PoolId {}. Aggregate worker {} devices connected from {}", worker.getPoolId(), worker.getName(), formatter.format(zdt));

            updateDevicesData(worker, fromDate);
        } catch (Throwable e) {
            LOGGER.error("Update device exception: {}", e.getMessage(), e);
        }
    }

    private void updateDevicesData(WorkerEntity worker, Instant dateFrom) {
        Map<String, DeviceEntity> savedDevices = deviceRepository.findByWorkerId(worker.getId()).stream()
                .collect(Collectors.toMap(DeviceEntity::getName, Function.identity()));
        List<DeviceEntity> devicesFromShareStatistic =
                deviceStatisticRepository.findDevicesFromShareStatistic(worker.getName(), worker.getPoolId(), dateFrom).stream()
                .distinct()
                .toList();

        List<DeviceEntity> deviceForUpdate = new ArrayList<>(devicesFromShareStatistic.size());
        List<DeviceEntity> newDevices = new ArrayList<>();

        devicesFromShareStatistic.forEach(device -> {
            if (savedDevices.containsKey(device.getName())) {
                DeviceEntity savedDevice = savedDevices.get(device.getName());

                savedDevice.setModifiedDate(Instant.now());
                savedDevice.setLastValidShareDate(device.getLastValidShareDate());
                savedDevice.setIsEnabled(device.getIsEnabled());

                deviceForUpdate.add(savedDevice);
            } else {
                newDevices.add(device);
            }
        });

        deviceRepository.updateDevices(deviceForUpdate);
        deviceRepository.addDevices(newDevices);
    }
}
