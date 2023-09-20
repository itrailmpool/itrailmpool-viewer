package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class WorkerEntity {

    private long id;
    private String name;
    private Instant createdTimestamp;
}
