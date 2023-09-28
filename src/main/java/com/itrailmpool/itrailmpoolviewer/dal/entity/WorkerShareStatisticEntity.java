package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigInteger;
import java.time.Instant;

@Data
public class WorkerShareStatisticEntity {

    private String poolId;
    private String workerName;
    private BigInteger totalAcceptedShares;
    private BigInteger totalRejectedShares;
    private Instant date;
    private Instant modifiedDate;

    @Override
    public String toString() {
        return "WorkerShareStatisticEntity{" +
                "poolId='" + poolId + '\'' +
                ", workerName='" + workerName + '\'' +
                ", totalAcceptedShares=" + totalAcceptedShares +
                ", totalRejectedShares=" + totalRejectedShares +
                ", date=" + date +
                ", modifiedDate=" + modifiedDate +
                '}';
    }
}
