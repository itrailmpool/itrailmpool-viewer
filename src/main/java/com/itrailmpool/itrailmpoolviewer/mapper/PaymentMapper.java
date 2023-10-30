package com.itrailmpool.itrailmpoolviewer.mapper;

import com.itrailmpool.itrailmpoolviewer.config.ExplorerLinkProperties;
import com.itrailmpool.itrailmpoolviewer.dal.entity.PaymentEntity;
import com.itrailmpool.itrailmpoolviewer.model.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PaymentMapper {

    protected ExplorerLinkProperties explorerLinkProperties;

    @Autowired
    void setExplorerLinkProperties(ExplorerLinkProperties explorerLinkProperties) {
        this.explorerLinkProperties = explorerLinkProperties;
    }

    @Mapping(target = "coin", source = "coin")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "addressInfoLink", source = "address", qualifiedByName = "addressToAddressInfoLink")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "transactionConfirmationData", source = "transactionConfirmationData")
    @Mapping(target = "transactionInfoLink", source = "transactionConfirmationData", qualifiedByName = "transactionHashToTransactionInfoLink")
    @Mapping(target = "created", source = "createdDate")
    public abstract PaymentDto toPaymentDto(PaymentEntity paymentEntity);

    public abstract List<PaymentDto> toPaymentDto(List<PaymentEntity> paymentEntity);


    @Named("addressToAddressInfoLink")
    protected String addressToAddressInfoLink(String address) {
        if (address == null) {
            return null;
        }

        String accountLink = explorerLinkProperties.getAccountLink();

        return String.format(accountLink, address);
    }

    @Named("transactionHashToTransactionInfoLink")
    protected String transactionHashToTransactionInfoLink(String transactionHash) {
        if (transactionHash == null) {
            return null;
        }

        String transactionLink = explorerLinkProperties.getTransactionLink();

        return String.format(transactionLink, transactionHash);
    }
}
