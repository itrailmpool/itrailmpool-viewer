package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class ShareStatistic {

    private String poolId;
    private Long blockHeight;
    private String miner;
    private String worker;
    private String userAgent;
    private BigDecimal difficulty;
    private BigDecimal networkDifficulty;
    private String ipAddress;
    private String source;
    private Instant created;
    private Boolean isValid;
    private String device;

}
