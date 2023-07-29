package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class WorkerPerformanceStatsContainer {
    private Instant created;
    private Map<String, WorkerPerformanceStats> workers;
}
