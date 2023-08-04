package com.itrailmpool.itrailmpoolviewer.model;

import lombok.Data;

import java.util.List;

@Data
public class PoolStatisticResponse {

    private List<AggregatedPoolStats> stats;
}
