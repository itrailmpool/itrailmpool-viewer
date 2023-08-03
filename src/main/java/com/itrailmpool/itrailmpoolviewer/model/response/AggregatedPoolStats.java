package com.itrailmpool.itrailmpoolviewer.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.DEFAULT_DATA_FORMAT_PATTERN;
import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.UTC_TIMEZONE;

@Data
public class AggregatedPoolStats {
    private BigDecimal poolHashrate;
    private Integer connectedMiners;
    private Integer validSharesPerSecond;
    private BigDecimal networkHashrate;
    private BigDecimal networkDifficulty;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATA_FORMAT_PATTERN, timezone = UTC_TIMEZONE)
    private Instant created;
}
