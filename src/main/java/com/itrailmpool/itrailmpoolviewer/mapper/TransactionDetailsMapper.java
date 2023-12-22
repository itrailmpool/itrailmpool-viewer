package com.itrailmpool.itrailmpoolviewer.mapper;

import com.itrailmpool.itrailmpoolviewer.client.rpc.javabitcoindrpcclient.BitcoindRpcClient;
import com.itrailmpool.itrailmpoolviewer.dal.entity.TransactionDetailsEntity;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionDetailsMapper {

    Logger LOGGER = LoggerFactory.getLogger(TransactionDetailsMapper.class);

    List<TransactionDetailsEntity> toTransactionDetailsEntity(List<BitcoindRpcClient.RawTransaction.Out> outs);

    default TransactionDetailsEntity toTransactionDetailsEntity(BitcoindRpcClient.RawTransaction.Out out) {
        if (out == null) {
            return null;
        }

        TransactionDetailsEntity transactionDetailsEntity = new TransactionDetailsEntity();
        transactionDetailsEntity.setAddress(toAddress(out.scriptPubKey()));
        transactionDetailsEntity.setAmount(BigDecimal.valueOf(out.value()));

        return transactionDetailsEntity;
    }

    default String toAddress(BitcoindRpcClient.RawTransaction.Out.ScriptPubKey scriptPubKey) {
        if (scriptPubKey == null) {
            return null;
        }

        List<String> addresses = scriptPubKey.addresses();
        if (addresses.size() != 1) {
            LOGGER.warn("Out addresses size: {}", addresses.size());
        }

        return addresses.get(0);
    }
}
