package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
public class PoolInfo {

    private String id;
    private ApiCoinConfig coin;
    private Map<Integer, PoolEndpoint> ports;
    private ApiPoolPaymentProcessingConfig paymentProcessing;
    private PoolShareBasedBanningConfig shareBasedBanning;
    private Integer clientConnectionTimeout;
    private Integer jobRebroadcastTimeout;
    private Integer blockRefreshInterval;
    private BigDecimal poolFeePercent;
    private String address;
    private String addressInfoLink;
    private PoolStats poolStats;
    private BlockchainStats networkStats;
    private List<MinerPerformanceStats> topMiners;
    private BigDecimal totalPaid;
    private BigInteger totalBlocks;
    private Instant lastPoolBlockTime;
    private BigDecimal poolEffort;
}
