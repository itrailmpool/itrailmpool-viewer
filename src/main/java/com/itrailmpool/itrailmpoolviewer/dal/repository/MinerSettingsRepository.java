package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerSettingsEntity;

import java.util.List;

public interface MinerSettingsRepository {

    MinerSettingsEntity findByPoolIdAndWorkerName(String poolId, String workerName);
    List<MinerSettingsEntity> findAll();
}
