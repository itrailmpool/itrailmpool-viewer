package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PaymentEntity {

    private Long id;
    private String poolId;
    private String coin;
    private String address;
    private BigDecimal amount;
    private String transactionConfirmationData;
    private Instant createdDate;
}
