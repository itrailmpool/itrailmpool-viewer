package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerSettings;

public interface MinerSettingsRepository {

    MinerSettings findByPoolIdAndWorkerName(String poolId, String workerName);
}
