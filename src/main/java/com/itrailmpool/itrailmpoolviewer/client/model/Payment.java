package com.itrailmpool.itrailmpoolviewer.client.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class Payment {

    private String coin;
    private String address;
    private String addressInfoLink;
    private BigDecimal amount;
    private String transactionConfirmationData;
    private String transactionInfoLink;
    private Instant created;
}

