package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class AggregatedPoolStats {
    private BigDecimal poolHashrate;
    private Integer connectedMiners;
    private Integer validSharesPerSecond;
    private BigDecimal networkHashrate;
    private BigDecimal networkDifficulty;
    private Instant created;
}
