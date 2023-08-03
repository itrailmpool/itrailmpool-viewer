package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class DeviceSharesStatisticEntity {

    private String workerName;
    private String deviceName;
    private Instant lastValidShareDate;
    private Boolean isOnline;
}
