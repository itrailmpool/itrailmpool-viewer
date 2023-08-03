package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkerHashRate {

    private BigDecimal currentHashRate;
    private BigDecimal hourlyAverageHashRate;
    private BigDecimal dailyAverageHashRate;
}
