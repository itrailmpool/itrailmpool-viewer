package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerEntity;

import java.util.List;

public interface WorkerRepository {

    List<WorkerEntity> findAll();
    WorkerEntity findByNameAndPoolId(String workerName, String poolId);
}
