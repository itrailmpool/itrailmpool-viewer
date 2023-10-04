package com.itrailmpool.itrailmpoolviewer.service;

import com.itrailmpool.itrailmpoolviewer.model.DeviceStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerCurrentStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerStatisticContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerStatisticDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkerStatisticService {

    WorkerStatisticContainerDto getWorkerStatistic(String poolId, String workerName);

    WorkerCurrentStatisticDto getWorkerCurrentStatistic(String poolId, String workerName);

    List<WorkerPerformanceStatsContainerDto> getWorkerPerformance(String poolId, String workerName);

    Page<WorkerStatisticDto> getWorkerStatistic(Pageable pageable, String poolId, String workerName);

    Page<DeviceStatisticDto> getDeviceStatistics(Pageable pageable, String poolId, String workerName, String deviceName);
}
