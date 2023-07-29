package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class WorkerDevicesStatistic {

    private String workerName;
    private Integer totalDevices;
    private Integer devicesOnline;
    private Integer devicesOffline;
    private List<DeviceStatistic> workerDevicesStatistic;
}