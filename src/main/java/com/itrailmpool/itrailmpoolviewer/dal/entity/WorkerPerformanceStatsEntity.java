package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class WorkerPerformanceStatsEntity {

    private String workerDeviceKey;
    private Instant created;
    private BigDecimal hashRate;
    private BigDecimal sharesPerSecond;
}
