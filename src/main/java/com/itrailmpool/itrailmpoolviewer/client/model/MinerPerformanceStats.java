package com.itrailmpool.itrailmpoolviewer.client.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MinerPerformanceStats {

    private String miner;
    private BigDecimal hashrate;
    private BigDecimal sharesPerSecond;
}
