package com.itrailmpool.itrailmpoolviewer.model;

import lombok.Data;

import java.util.List;

@Data
public class PoolStatisticContainerDto {

    private List<AggregatedPoolStatsDto> stats;
}
