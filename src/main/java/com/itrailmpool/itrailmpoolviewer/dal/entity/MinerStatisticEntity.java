package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class MinerStatisticEntity {

    private String poolId;
    private String miner;
    private String workerDeviceCode;
    private BigDecimal hashRate;
    private BigDecimal sharePerSecond;
    private Instant created;
}
