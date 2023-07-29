package com.itrailmpool.itrailmpoolviewer.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Accessors(chain = true)
public class DeviceStatistic {
    private String workerName;
    private String deviceName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant lastShareDate;
    private BigDecimal currentHashrate;
    private BigDecimal hourlyAverageHashrate;
    private BigDecimal dailyAverageHashrate;
    private String deviceStatus;
}