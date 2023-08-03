package com.itrailmpool.itrailmpoolviewer.client.model;

import lombok.Data;

import java.math.BigInteger;
import java.time.Instant;

@Data
public class PoolStats {

    private Instant lastPoolBlockTime;
    private Integer connectedMiners;
    private BigInteger poolHashrate;
    private BigInteger sharesPerSecond;
}
