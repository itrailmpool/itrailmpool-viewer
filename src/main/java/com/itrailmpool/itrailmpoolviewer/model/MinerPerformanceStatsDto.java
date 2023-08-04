package com.itrailmpool.itrailmpoolviewer.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MinerPerformanceStatsDto {

    private String miner;
    private BigDecimal hashrate;
    private BigDecimal sharesPerSecond;
}
