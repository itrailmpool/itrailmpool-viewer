package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.TransactionDetailsEntity;

import java.util.List;

public interface TransactionDetailsRepository {

    List<TransactionDetailsEntity> findTransactionDetails(Long transactionId);

    void insert(List<TransactionDetailsEntity> transactionDetailsEntities);
}
