package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

@Data
public class WorkerStatisticEntity {

    private String workerName;
    private Instant date;
    private BigDecimal averageHashRate;
    private BigDecimal averageSharesPerSecond;
    private BigInteger totalAcceptedShares;
    private BigInteger totalRejectedShares;
    private BigDecimal totalPayment;
}
