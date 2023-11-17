package com.vincentcodes.net;

import java.io.IOException;

import com.vincentcodes.net.message.SocksRequest;

public interface EndpointConnector {
    ConnectorResult connectToEndpoint(SocksRequest request) throws IOException;
}
