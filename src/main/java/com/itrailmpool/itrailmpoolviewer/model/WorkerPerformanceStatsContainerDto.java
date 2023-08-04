package com.itrailmpool.itrailmpoolviewer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.DEFAULT_DATA_FORMAT_PATTERN;
import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.UTC_TIMEZONE;

@Data
public class WorkerPerformanceStatsContainerDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATA_FORMAT_PATTERN, timezone = UTC_TIMEZONE)
    private Instant created;
    private Map<String, WorkerPerformanceStatsDto> workers;
}
