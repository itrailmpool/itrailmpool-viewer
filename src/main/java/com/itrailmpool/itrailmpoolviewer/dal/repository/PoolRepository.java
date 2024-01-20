package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.PoolEntity;

import java.util.List;

public interface PoolRepository {

    List<PoolEntity> findAll();
}
