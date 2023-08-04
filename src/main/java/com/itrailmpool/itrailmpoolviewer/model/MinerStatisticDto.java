package com.itrailmpool.itrailmpoolviewer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.DEFAULT_DATA_FORMAT_PATTERN;
import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.UTC_TIMEZONE;

@Data
public class MinerStatisticDto {

    private BigDecimal pendingShares;
    private BigDecimal pendingBalance;
    private BigDecimal totalPaid;
    private BigDecimal todayPaid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATA_FORMAT_PATTERN, timezone = UTC_TIMEZONE)
    private Instant lastPayment;
    private String lastPaymentLink;
    private WorkerPerformanceStatsContainerDto performance;
    private List<WorkerPerformanceStatsContainerDto> performanceSamples;
}
