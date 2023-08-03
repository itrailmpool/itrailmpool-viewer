package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigInteger;
import java.time.Instant;

@Data
public class WorkerShareStatisticEntity {

    private String workerName;
    private BigInteger totalAcceptedShares;
    private BigInteger totalRejectedShares;
    private Instant date;
}
