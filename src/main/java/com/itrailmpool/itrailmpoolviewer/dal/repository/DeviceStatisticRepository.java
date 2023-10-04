package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceStatisticEntity;

import java.time.Instant;
import java.util.List;

public interface DeviceStatisticRepository {

    List<DeviceStatisticEntity> getWorkerDevicesStatistic(String poolId, String workerName);

    List<String> getWorkerDevices(String workerName);

    Integer getActiveWorkersCount(String poolId);

    List<DeviceEntity> findDevicesFromShareStatistic(String workerName, String poolId);
}
