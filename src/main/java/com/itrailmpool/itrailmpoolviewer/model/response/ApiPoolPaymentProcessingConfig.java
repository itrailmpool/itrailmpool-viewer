package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApiPoolPaymentProcessingConfig {
    private Boolean enabled;
    private BigDecimal minimumPayment; // in pool-base-currency (ie. Bitcoin, not Satoshis)
    private String payoutScheme;
}
