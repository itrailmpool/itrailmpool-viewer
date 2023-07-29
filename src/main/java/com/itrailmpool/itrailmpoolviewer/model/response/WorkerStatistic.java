package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;


import java.math.BigDecimal;
import java.time.Instant;

@Data
@Accessors(chain = true)
public class WorkerStatistic {

    private String workerName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant date;

    private BigDecimal averageHashrate;
    private Integer acceptedShares;
    private Integer unacceptedShares;
    private BigDecimal totalPayment;
}