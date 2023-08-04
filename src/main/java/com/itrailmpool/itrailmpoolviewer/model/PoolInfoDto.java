package com.itrailmpool.itrailmpoolviewer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.DEFAULT_DATA_FORMAT_PATTERN;
import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.UTC_TIMEZONE;

@Data
public class PoolInfoDto {

    private String id;
    private ApiCoinConfigDto coin;
    private Map<Integer, PoolEndpointDto> ports;
    private ApiPoolPaymentProcessingConfigDto paymentProcessing;
    private PoolShareBasedBanningConfigDto shareBasedBanning;
    private Integer clientConnectionTimeout;
    private Integer jobRebroadcastTimeout;
    private Integer blockRefreshInterval;
    private BigDecimal poolFeePercent;
    private String address;
    private String addressInfoLink;
    private PoolStatsDto poolStats;
    private BlockchainStatsDto networkStats;
    private List<MinerPerformanceStatsDto> topMiners;
    private BigDecimal totalPaid;
    private BigInteger totalBlocks;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATA_FORMAT_PATTERN, timezone = UTC_TIMEZONE)
    private Instant lastPoolBlockTime;
    private BigDecimal poolEffort;
}
