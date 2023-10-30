package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.PaymentEntity;

import java.util.List;

public interface PaymentRepository {

    List<PaymentEntity> findByPoolIdAndWorkerName(String poolId, String workerName);
}
