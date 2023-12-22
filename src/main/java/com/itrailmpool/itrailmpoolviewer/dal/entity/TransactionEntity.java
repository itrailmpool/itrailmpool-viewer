package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransactionEntity {

    private Long id;
    private String poolId;
    private String hash;
    private BigDecimal amount;
    private Instant createdDate;
    private Instant modifiedDate;
}