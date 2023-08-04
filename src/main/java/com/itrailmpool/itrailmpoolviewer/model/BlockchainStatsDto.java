package com.itrailmpool.itrailmpoolviewer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.DEFAULT_DATA_FORMAT_PATTERN;
import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.UTC_TIMEZONE;

@Data
public class BlockchainStatsDto {

    private String networkType;
    private BigDecimal networkHashrate;
    private BigDecimal networkDifficulty;
    private String nextNetworkTarget;
    private String nextNetworkBits;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATA_FORMAT_PATTERN, timezone = UTC_TIMEZONE)
    private Instant lastNetworkBlockTime;
    private BigInteger blockHeight;
    private Integer connectedPeers;
    private String rewardType;
}
