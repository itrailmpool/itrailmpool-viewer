package com.itrailmpool.itrailmpoolviewer.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class WorkerStatisticContainer {

    private String poolId;
    private String workerName;
    private WorkerHashRateDto workerHashRate;
    private List<WorkerStatisticDto> workerStatistics;
    private WorkerDevicesStatisticDto workerDevicesStatistic;
}



