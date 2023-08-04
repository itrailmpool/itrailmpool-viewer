package com.itrailmpool.itrailmpoolviewer.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VarDiffConfig {

    /**
     * Minimum difficulty
     */
    private BigDecimal minDiff;
    /**
     * Network difficulty will be used if it is lower than this
     */
    private BigDecimal maxDiff;

    /**
     * Do not alter difficulty by more than this during a single retarget in either direction
     */
    private BigDecimal maxDelta;

    /**
     * Try to get 1 share per this many seconds
     */
    private BigDecimal targetTime;

    /**
     * Check to see if we should retarget every this many seconds
     */
    private BigDecimal retargetTime;

    /**
     * Allow submission frequency to diverge this much (%) from target time without triggering a retarget
     */
    private BigDecimal variancePercent;
}
