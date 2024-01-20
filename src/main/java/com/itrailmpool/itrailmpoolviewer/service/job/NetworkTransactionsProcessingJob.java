package com.itrailmpool.itrailmpoolviewer.service.job;

import com.itrailmpool.itrailmpoolviewer.client.rpc.javabitcoindrpcclient.BitcoindRpcClient;
import com.itrailmpool.itrailmpoolviewer.dal.entity.PaymentEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.PoolEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.TransactionDetailsEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.TransactionEntity;
import com.itrailmpool.itrailmpoolviewer.dal.repository.PaymentRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.PoolRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.TransactionDetailsRepository;
import com.itrailmpool.itrailmpoolviewer.dal.repository.TransactionRepository;
import com.itrailmpool.itrailmpoolviewer.mapper.TransactionDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NetworkTransactionsProcessingJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkTransactionsProcessingJob.class);

    private final TransactionRepository transactionRepository;
    private final TransactionDetailsRepository transactionDetailsRepository;
    private final PaymentRepository paymentRepository;
    private final PoolRepository poolRepository;
    private final TransactionTemplate transactionTemplate;
    private final BitcoindRpcClient bitcoindRpcClient;
    private final TransactionDetailsMapper transactionDetailsMapper;


    @Scheduled(initialDelay = 1, fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    private void runNetworkTransactionsProcessingJob() {
        LOGGER.info("New transactions processing started");

        transactionTemplate.executeWithoutResult(status -> poolRepository.findAll().stream()
                .map(PoolEntity::getPoolId)
                .forEach(this::processPoolTransactions));

        LOGGER.info("New transactions processing finished");
    }

    private void processPoolTransactions(String poolId) {
        try {
            TransactionEntity lastTransaction = transactionRepository.findLastTransactionByPoolId(poolId);
            List<PaymentEntity> payments;

            if (lastTransaction == null) {
                payments = paymentRepository.findByPoolId(poolId);
            } else {
                payments = paymentRepository.findByPoolIdAndCreatedDateAfter(poolId, lastTransaction.getCreatedDate());
            }

            Map<String, PaymentEntity> distinctPayments = new HashMap<>();
            payments.forEach(payment -> distinctPayments.putIfAbsent(payment.getTransactionConfirmationData(), payment));

            distinctPayments.values().stream()
                    .sorted(Comparator.comparing(PaymentEntity::getCreatedDate))
                    .forEach(this::processTransaction);
        } catch (Throwable e) {
            LOGGER.error("New transactions processing exception: {}", e.getMessage(), e);
        }
    }


    private void processTransaction(PaymentEntity payment) {
        String hash = payment.getTransactionConfirmationData();
        BitcoindRpcClient.RawTransaction rawTransaction = bitcoindRpcClient.getRawTransaction(hash);

        if (rawTransaction == null) {
            LOGGER.warn("Transaction not found in blockchain network: hash={}, poolId={}", hash, payment.getPoolId());
            return;
        }

        List<TransactionDetailsEntity> transactionDetails = transactionDetailsMapper.toTransactionDetailsEntity(rawTransaction.vOut());
        BigDecimal transactionAmount = transactionDetails.stream()
                .map(TransactionDetailsEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        TransactionEntity transaction = new TransactionEntity();
        transaction.setPoolId(payment.getPoolId());
        transaction.setHash(hash);
        transaction.setAmount(transactionAmount);
        transaction.setCreatedDate(payment.getCreatedDate());
        transaction.setModifiedDate(Instant.now());

        Long transactionId = transactionRepository.insert(transaction);

        transactionDetails.forEach(details -> details.setTransactionId(transactionId));

        transactionDetailsRepository.insert(transactionDetails);
    }
}
