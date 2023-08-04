package com.itrailmpool.itrailmpoolviewer.client.model;

import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsDto;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class WorkerPerformanceStatsContainer {

    private Instant created;
    private Map<String, WorkerPerformanceStatsDto> workers;
}
