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

public class Socks5Connection implements Runnable{
    public static byte[] SUPPORTED_METHODS = {
        SelectionMessage.Methods.NO_AUTHENTICATION_REQUIRED,
        SelectionMessage.Methods.USERNAME_PASSWORD,
    };

    private Socket client;
    private InputStream bis;
    private OutputStream os;

    private UserPassAuthenticator authenticator = ($) -> true;

    public Socks5Connection(Socket client) throws IOException{
        this.client = client;
        bis = new BufferedInputStream(client.getInputStream());
        os = client.getOutputStream();
    }

    @Override
    public void run() {
        Socks5Server.LOGGER.info("Received connection from " + client.getRemoteSocketAddress());
        try {
            byte[] methods = negotiate();
            // if auth, use UserPassMessage.parse, UserPassMessage.serverReply
            byte selectedMethod = selectMethod(methods);
            SelectionMessage.streamTo(os, SelectionMessage.createServerMessage(selectedMethod));

            subnegotiate(selectedMethod);

            // TODO: Test echo server
            SocksRequest request = SocksRequest.parse(bis);

            SocksReply reply = new SocksReply(
                SocksReply.StatusCodes.SUCCEEDED, SocksReply.AddrType.IPv4, request.dstAddr, request.dstPort);
            SocksReply.streamTo(os, reply);

            byte[] buffer = new byte[1024];
            int count = bis.read(buffer);
            System.out.println(new String(buffer));

            os.write(buffer, 0, count);
        } catch (IOException e) {
            // if(e.getMessage().startsWith("Connection reset")) return;
            e.printStackTrace();
            throw new UncheckedIOException(e);
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * @return all methods that the client wants 
     */
    private byte[] negotiate() throws IOException{
        return SelectionMessage.parse(bis, true).methods;
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
