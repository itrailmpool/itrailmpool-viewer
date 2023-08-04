package com.itrailmpool.itrailmpoolviewer.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkerHashRateDto {

    private BigDecimal currentHashRate;
    private BigDecimal hourlyAverageHashRate;
    private BigDecimal dailyAverageHashRate;
}
