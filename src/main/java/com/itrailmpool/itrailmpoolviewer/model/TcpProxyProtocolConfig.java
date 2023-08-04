package com.itrailmpool.itrailmpoolviewer.model;

import lombok.Data;

import java.util.List;

@Data
public class TcpProxyProtocolConfig {

    /**
     * Enable for client IP addresses to be detected when using a load balancer with TCP proxy protocol enabled, such as HAProxy.
     */
    private boolean enable;

    /**
     * Terminate connections that are not beginning with a proxy-protocol header
     */
    private boolean mandatory;

    /**
     * List of IP addresses of valid proxy addresses. If absent, localhost is used
     */
    private List<String> proxyAddresses;
}
