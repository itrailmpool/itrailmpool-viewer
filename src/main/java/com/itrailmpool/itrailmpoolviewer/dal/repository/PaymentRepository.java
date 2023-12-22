package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.PaymentEntity;

import java.time.Instant;
import java.util.List;

public interface PaymentRepository {

    List<PaymentEntity> findByPoolIdAndWorkerName(String poolId, String workerName);

    List<PaymentEntity> findByPoolIdAndCreatedDateAfter(String poolId, Instant date);

    List<PaymentEntity> findByPoolId(String poolId);
}
