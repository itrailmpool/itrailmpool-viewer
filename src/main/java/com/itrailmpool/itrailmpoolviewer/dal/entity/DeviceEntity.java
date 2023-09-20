package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class DeviceEntity {

    private long id;
    private String name;
    private long workerId;
    private Instant created;
    private Instant modified;
    private boolean isEnabled;
}
