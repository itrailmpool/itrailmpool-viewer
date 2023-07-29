package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkerPerformanceStats {
    private BigDecimal hashrate;
    private BigDecimal sharesPerSecond;
}
