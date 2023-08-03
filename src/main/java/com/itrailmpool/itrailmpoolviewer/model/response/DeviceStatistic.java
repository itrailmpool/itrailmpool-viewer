package com.itrailmpool.itrailmpoolviewer.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;

import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.DEFAULT_DATA_FORMAT_PATTERN;
import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.UTC_TIMEZONE;

@Data
@Accessors(chain = true)
public class DeviceStatistic {

    private String workerName;
    private String deviceName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATA_FORMAT_PATTERN, timezone = UTC_TIMEZONE)
    private Instant lastShareDate;
    private BigDecimal currentHashRate;
    private BigDecimal hourlyAverageHashRate;
    private BigDecimal dailyAverageHashRate;
    private Boolean isOnline;
}