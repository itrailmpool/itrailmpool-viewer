package com.itrailmpool.itrailmpoolviewer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.LOCAL_DATA_FORMAT_PATTERN;

@Data
@Accessors(chain = true)
public class WorkerStatisticDto {

    private String workerName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LOCAL_DATA_FORMAT_PATTERN)
    private LocalDate date;
    private BigDecimal averageHashRate;
    private BigDecimal averageSharesPerSecond;
    private BigInteger totalAcceptedShares;
    private BigInteger totalRejectedShares;
    private BigDecimal totalPayment;
}