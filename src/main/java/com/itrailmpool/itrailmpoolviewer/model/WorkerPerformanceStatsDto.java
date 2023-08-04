package com.itrailmpool.itrailmpoolviewer.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkerPerformanceStatsDto {

    private BigDecimal hashrate;
    private BigDecimal sharesPerSecond;
}
