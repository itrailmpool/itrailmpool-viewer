package com.itrailmpool.itrailmpoolviewer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.DEFAULT_DATA_FORMAT_PATTERN;
import static com.itrailmpool.itrailmpoolviewer.config.ApplicationConfig.UTC_TIMEZONE;

@Data
public class BlockDto {

    private String poolId;
    private BigInteger blockHeight;
    private BigDecimal networkDifficulty;
    private String status;
    private String type;
    private BigDecimal confirmationProgress;
    private BigDecimal effort;
    private String transactionConfirmationData;
    private BigDecimal reward;
    private String infoLink;
    private String hash;
    private String miner;
    private String source;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATA_FORMAT_PATTERN, timezone = UTC_TIMEZONE)
    private Instant created;
}
