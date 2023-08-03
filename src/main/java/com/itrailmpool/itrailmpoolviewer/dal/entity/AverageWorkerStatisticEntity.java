package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class AverageWorkerStatisticEntity {

    private Instant date;
    private BigDecimal averageHashRate;
    private BigDecimal averageSharePerSecond;
}
