package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PoolShareBasedBanningConfig {
    private Boolean enabled ;
    private Integer checkThreshold ; // Check stats when this many shares have been submitted
    private BigDecimal invalidPercent ; // What percent of invalid shares triggers ban
    private Integer time ; // How many seconds to ban worker for
}
