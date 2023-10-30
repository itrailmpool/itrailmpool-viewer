package com.itrailmpool.itrailmpoolviewer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("explorer")
public class ExplorerLinkProperties {

    private String blockLink;
    private String transactionLink;
    private String accountLink;
}
