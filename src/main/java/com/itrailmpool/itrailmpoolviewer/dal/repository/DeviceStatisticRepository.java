package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceStatisticEntity;

import java.util.List;

public interface DeviceStatisticRepository {

    List<DeviceStatisticEntity> getWorkerDevicesStatistic(String poolId, String workerName);

    List<String> getWorkerDevices(String workerName);

    Integer getActiveWorkersCount(String poolId);
}
