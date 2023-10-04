package com.itrailmpool.itrailmpoolviewer.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class WorkerCurrentStatisticDto {

    private String workerName;
    private BigDecimal currentHashRate;
    private BigDecimal hourlyAverageHashRate;
    private BigDecimal dailyAverageHashRate;
    private Long totalDevices;
    private Long devicesOnline;
    private Long devicesOffline;
}
