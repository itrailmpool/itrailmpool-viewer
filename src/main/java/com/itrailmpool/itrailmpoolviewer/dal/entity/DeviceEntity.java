package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class DeviceEntity {

    private Long id;
    private String name;
    private Long workerId;
    private Instant createdDate;
    private Instant modifiedDate;
    private Instant lastValidShareDate;
    private Boolean isEnabled;


}
