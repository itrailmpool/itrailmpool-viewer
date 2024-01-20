package com.itrailmpool.itrailmpoolviewer.mapper;

import com.itrailmpool.itrailmpoolviewer.config.ExplorerLinkProperties;
import com.itrailmpool.itrailmpoolviewer.dal.entity.PaymentEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.TransactionEntity;
import com.itrailmpool.itrailmpoolviewer.model.PaymentDto;
import com.itrailmpool.itrailmpoolviewer.model.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {


    protected ExplorerLinkProperties explorerLinkProperties;

    @Autowired
    void setExplorerLinkProperties(ExplorerLinkProperties explorerLinkProperties) {
        this.explorerLinkProperties = explorerLinkProperties;
    }

    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "transactionConfirmationData", source = "hash")
    @Mapping(target = "transactionInfoLink", source = "hash", qualifiedByName = "transactionHashToTransactionInfoLink")
    @Mapping(target = "created", source = "createdDate")
    public abstract TransactionDto toTransactionDto(TransactionEntity transactionEntity);

    public abstract List<TransactionDto> toTransactionDto(List<TransactionEntity> transactionEntities);

    @Named("transactionHashToTransactionInfoLink")
    protected String transactionHashToTransactionInfoLink(String transactionHash) {
        if (transactionHash == null) {
            return null;
        }

        String transactionLink = explorerLinkProperties.getTransactionLink();

        return String.format(transactionLink, transactionHash);
    }
}
