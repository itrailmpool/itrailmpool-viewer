package com.itrailmpool.itrailmpoolviewer.client.model;

import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerPerformanceStatsEntity;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class WorkerPerformanceStatsContainer {

    private Instant created;
    private Map<String, WorkerPerformanceStatsEntity> workers;
}
