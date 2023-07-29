package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

@Data
public class Block {
    private String poolId;
    private BigInteger blockHeight;
    private BigDecimal networkDifficulty;
    private String status;
    private String type;
    private BigDecimal confirmationProgress;
    private BigDecimal effort;
    private String transactionConfirmationData;
    private BigDecimal reward;
    private String infoLink;
    private String hash;
    private String miner;
    private String source;
    private Instant created;
}
