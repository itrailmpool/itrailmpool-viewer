package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class MinerSettings {
    private String poolId;
    private String address;
    private BigDecimal paymentThreshold;
    private Instant created;
    private Instant updated;
    private String workerName;
    private String password;
}
