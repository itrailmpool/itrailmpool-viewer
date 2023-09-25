package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class WorkerStatisticEntity {

    private String poolId;
    private String workerName;
    private LocalDate date;
    private BigDecimal averageHashRate;
    private BigInteger totalAcceptedShares;
    private BigInteger totalRejectedShares;
    private BigDecimal totalPayment;
    private Instant modifiedDate;
}
