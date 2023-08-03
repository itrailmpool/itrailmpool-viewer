package com.itrailmpool.itrailmpoolviewer.client.model;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

@Data
public class BlockchainStats {

    private String networkType;
    private BigDecimal networkHashrate;
    private BigDecimal networkDifficulty;
    private String nextNetworkTarget;
    private String nextNetworkBits;
    private Instant lastNetworkBlockTime;
    private BigInteger blockHeight;
    private Integer connectedPeers;
    private String rewardType;
}
