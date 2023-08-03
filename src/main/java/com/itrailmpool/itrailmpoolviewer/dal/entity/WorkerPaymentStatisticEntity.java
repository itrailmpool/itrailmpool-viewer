package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class WorkerPaymentStatisticEntity {

    private String workerName;
    private Instant date;
    private BigDecimal totalPayments;
}
