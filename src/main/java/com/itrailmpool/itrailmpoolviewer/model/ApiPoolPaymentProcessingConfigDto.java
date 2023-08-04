package com.itrailmpool.itrailmpoolviewer.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApiPoolPaymentProcessingConfigDto {

    private Boolean enabled;
    private BigDecimal minimumPayment; // in pool-base-currency (ie. Bitcoin, not Satoshis)
    private String payoutScheme;
}
