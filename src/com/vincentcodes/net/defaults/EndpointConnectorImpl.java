package com.vincentcodes.net.defaults;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import com.vincentcodes.net.ConnectorResult;
import com.vincentcodes.net.EndpointConnector;
import com.vincentcodes.net.Socks5Server;
import com.vincentcodes.net.message.SocksRequest;
import com.vincentcodes.net.utils.IOContainer;

public class EndpointConnectorImpl implements EndpointConnector {

    @Override
    public ConnectorResult connectToEndpoint(SocksRequest request) throws IOException {
        InetAddress inetAddress;
        if(request.addrType == SocksRequest.AddrType.DOMAINNAME){
            inetAddress = InetAddress.getByName(new String(request.dstAddr).intern());
        }else{
            inetAddress = InetAddress.getByAddress(request.dstAddr);
        }
        Socks5Server.LOGGER.debug("Client dst: " + inetAddress);
        Socket endpoint = new Socket(inetAddress, request.dstPort);
        return new ConnectorResult(
            new IOContainer(
                endpoint, 
                new BufferedInputStream(endpoint.getInputStream()), 
                endpoint.getOutputStream()
            ), 
            inetAddress.getAddress(),
            request.dstPort
        );
    }
    
}
