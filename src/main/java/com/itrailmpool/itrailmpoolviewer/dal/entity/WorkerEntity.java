package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class WorkerEntity {

    private Long id;
    private String name;
    private String poolId;
    private Instant creationDate;
}
