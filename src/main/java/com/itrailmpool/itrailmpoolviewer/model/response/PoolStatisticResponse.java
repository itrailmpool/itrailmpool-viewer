package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;

import java.util.List;

@Data
public class PoolStatisticResponse {
    private List<AggregatedPoolStats> stats;
}
