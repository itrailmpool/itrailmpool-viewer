package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDetailsEntity {

    private Long id;
    private Long transactionId;
    private String address;
    private BigDecimal amount;
}
