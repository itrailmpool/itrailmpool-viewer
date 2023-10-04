package com.itrailmpool.itrailmpoolviewer.client.model;

import lombok.Data;

import java.util.List;

@Data
public class PoolStatisticResponse {

    private List<AggregatedPoolStats> stats;
}
