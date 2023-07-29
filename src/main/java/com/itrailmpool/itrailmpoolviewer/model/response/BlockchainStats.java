package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

@Data
public class BlockchainStats {
    private String NetworkType;
    private BigDecimal NetworkHashrate;
    private BigDecimal NetworkDifficulty;
    private String NextNetworkTarget;
    private String NextNetworkBits;
    private Instant LastNetworkBlockTime;
    private BigInteger BlockHeight;
    private Integer ConnectedPeers;
    private String RewardType;
}
