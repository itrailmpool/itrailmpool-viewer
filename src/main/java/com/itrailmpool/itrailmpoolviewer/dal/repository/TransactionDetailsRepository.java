package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.TransactionDetailsEntity;

import java.util.List;

public interface TransactionDetailsRepository {

    List<TransactionDetailsEntity> findTransactionDetails(Long transactionId);

    TransactionDetailsEntity findByTransactionIdAndAddress(Long transactionId, String address);

    void insert(List<TransactionDetailsEntity> transactionDetailsEntities);
}
