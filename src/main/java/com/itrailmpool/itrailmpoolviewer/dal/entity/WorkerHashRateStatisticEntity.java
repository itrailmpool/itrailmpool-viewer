package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class WorkerHashRateStatisticEntity {

    private String workerName;
    private BigDecimal averageHashRate;
    private Instant date;
}
