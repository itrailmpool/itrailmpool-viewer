package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.TransactionEntity;

import java.util.List;

public interface TransactionRepository {

    TransactionEntity findLastTransactionByPoolId(String poolId);

    List<TransactionEntity> findAllByPoolId(String poolId);

    List<TransactionEntity> findAllByPoolId(String poolId, int pageNumber, int pageSize);

    Long insert(TransactionEntity transaction);
}
