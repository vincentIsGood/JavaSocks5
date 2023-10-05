package com.vincentcodes.net.defaults;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import com.vincentcodes.net.EndpointConnector;
import com.vincentcodes.net.message.SocksRequest;
import com.vincentcodes.net.utils.IOContainer;

public class EndpointConnectorImpl implements EndpointConnector {

    @Override
    public IOContainer connectToEndpoint(SocksRequest request) throws IOException {
        InetAddress dstAddr = InetAddress.getByAddress(request.dstAddr);
        Socket endpoint = new Socket(dstAddr, request.dstPort);
        return new IOContainer(endpoint, new BufferedInputStream(endpoint.getInputStream()), endpoint.getOutputStream());
    }
    
}
