package com.itrailmpool.itrailmpoolviewer.client.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Data
public class MinerStatisticResponse {
    private BigDecimal pendingShares;
    private BigDecimal pendingBalance;
    private BigDecimal totalPaid;
    private BigDecimal todayPaid;
    private Instant lastPayment;
    private String lastPaymentLink;
    private WorkerPerformanceStatsContainer performance;
    private List<WorkerPerformanceStatsContainer> performanceSamples;
}
