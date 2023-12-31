package com.vincentcodes.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;

import com.vincentcodes.net.auth.BadCredentials;
import com.vincentcodes.net.auth.UserPassAuthenticator;
import com.vincentcodes.net.auth.UserPassMessage;
import com.vincentcodes.net.defaults.EndpointConnectorImpl;
import com.vincentcodes.net.defaults.ProxyDataTransferHandlerImpl;
import com.vincentcodes.net.message.SelectionMessage;
import com.vincentcodes.net.message.SocksReply;
import com.vincentcodes.net.message.SocksRequest;
import com.vincentcodes.net.utils.IOContainer;

public class Socks5Connection implements Runnable{

    private IOContainer client;
    private InputStream bis;
    private OutputStream os;

    private UserPassAuthenticator authenticator = ($) -> true;
    private EndpointConnector endpointConnector = new EndpointConnectorImpl();
    private ProxyTunnelHandler proxyTunnelHandler = new ProxyDataTransferHandlerImpl();

    public Socks5Connection(Socket client) throws IOException{
        bis = new BufferedInputStream(client.getInputStream());
        os = client.getOutputStream();
        this.client = new IOContainer(client, bis, os);
    }

    @Override
    public void run() {
        Socks5Server.LOGGER.info("Received connection from " + client.getSocket().getRemoteSocketAddress());
        try {
            byte[] methods = negotiate();
            byte selectedMethod = selectMethod(methods);
            SelectionMessage responseMessage = SelectionMessage.createServerMessage(selectedMethod);
            Socks5Server.LOGGER.debug("Server: " + responseMessage.toString());
            SelectionMessage.streamTo(os, responseMessage);

            subnegotiate(selectedMethod);

            SocksRequest request = SocksRequest.parse(bis);
            Socks5Server.LOGGER.debug("Client: " + request.toString());
            ConnectorResult connectorResult = endpointConnector.connectToEndpoint(request);
            IOContainer endpoint = connectorResult.socket;

            SocksReply reply = new SocksReply(
                SocksReply.StatusCodes.SUCCEEDED, SocksReply.AddrType.IPv4, connectorResult.addr, connectorResult.port);
            Socks5Server.LOGGER.debug("Server: " + reply.toString());
            SocksReply.streamTo(os, reply);

            proxyTunnelHandler.takeover(client, endpoint);
        } catch (IOException e) {
            if(e.getMessage().startsWith("Connection reset")
            || e.getMessage().startsWith("An established")) return;
            e.printStackTrace();
            throw new UncheckedIOException(e);
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally{
            Socks5Server.LOGGER.debug("Connection closed: " + client.getSocket().getRemoteSocketAddress());
        }
    }

    /**
     * @return all methods that the client wants 
     */
    private byte[] negotiate() throws IOException{
        SelectionMessage selectionMessage = SelectionMessage.parse(bis, true);
        Socks5Server.LOGGER.debug("Client: " + selectionMessage.toString());
        return selectionMessage.methods;
    }

    private byte selectMethod(byte[] methods){
        byte selectedMethod = SelectionMessage.Methods.NO_ACCEPTABLE_METHODS;
        for(int i = 0; i < methods.length; i++){
            if(methods[i] == SelectionMessage.Methods.USERNAME_PASSWORD){
                selectedMethod = SelectionMessage.Methods.USERNAME_PASSWORD;
                break;
            }
            if(methods[i] == SelectionMessage.Methods.NO_AUTHENTICATION_REQUIRED)
                selectedMethod = SelectionMessage.Methods.NO_AUTHENTICATION_REQUIRED;
        }
        return selectedMethod;
    }

    private void subnegotiate(byte selectedMethod) throws IOException, BadCredentials{
        if(selectedMethod == SelectionMessage.Methods.NO_AUTHENTICATION_REQUIRED){
            return;
        }

        if(selectedMethod == SelectionMessage.Methods.USERNAME_PASSWORD){
            UserPassMessage userPassMessage = UserPassMessage.parse(bis);
            if(!authenticator.authenticate(userPassMessage)){
                UserPassMessage.serverReply(os, false);
                throw new BadCredentials(userPassMessage.username, userPassMessage.password);
            }
            UserPassMessage.serverReply(os, true);
            return;
        }
    }
}
