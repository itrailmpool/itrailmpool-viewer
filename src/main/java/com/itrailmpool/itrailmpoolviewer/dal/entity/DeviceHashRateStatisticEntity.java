package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeviceHashRateStatisticEntity {

    private String workerName;
    private String deviceName;
    private BigDecimal currentHashRate;
    private BigDecimal hourlyAverageHashRate;
    private BigDecimal dailyAverageHashRate;
}
