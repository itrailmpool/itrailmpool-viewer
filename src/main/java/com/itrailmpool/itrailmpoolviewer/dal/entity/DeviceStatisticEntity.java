package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Accessors(chain = true)
public class DeviceStatisticEntity {

    private String workerName;
    private String deviceName;
    private Instant lastShareDate;
    private Boolean isOnline;
    private BigDecimal currentHashRate;
    private BigDecimal hourlyAverageHashRate;
    private BigDecimal dailyAverageHashRate;
}
