package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerSettingsEntity;

public interface MinerSettingsRepository {

    MinerSettingsEntity findByPoolIdAndWorkerName(String poolId, String workerName);
}
