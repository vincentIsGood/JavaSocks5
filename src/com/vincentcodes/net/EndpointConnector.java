package com.vincentcodes.net;

import java.io.IOException;

import com.vincentcodes.net.utils.IOContainer;

public interface EndpointConnector {
    IOContainer connectToEndpoint(SocksRequest request) throws IOException;
}