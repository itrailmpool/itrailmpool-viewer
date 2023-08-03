package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkerHashRateEntity {

    private BigDecimal currentHashRate;
    private BigDecimal hourlyAverageHashRate;
    private BigDecimal dailyAverageHashRate;
}
