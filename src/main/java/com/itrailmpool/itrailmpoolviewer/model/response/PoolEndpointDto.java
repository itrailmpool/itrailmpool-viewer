package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PoolEndpointDto {

    private String listenAddress;
    private String name;
    private BigDecimal difficulty;
    private TcpProxyProtocolConfig tcpProxyProtocol;
    private VarDiffConfig varDiff;

    /**
     * Enable Transport layer security (TLS)
     * If set to true, you must specify values for either TlsPemFile or TlsPfxFile
     * If TlsPemFile does not include the private key, TlsKeyFile is also required
     */
    private Boolean tls;

    /**
     * Enable TLS sniffing
     * Check incoming stratum connections for TLS handshake indicator and default to non-TLS if not present
     */
    private Boolean tlsAuto;

    /**
     * PKCS certificate file
     */
    private Boolean tlsPfxFile;

    /**
     * Certificate file password
     */
    private String tlsPfxPassword;
}
