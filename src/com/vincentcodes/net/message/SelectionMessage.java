package com.vincentcodes.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @see https://datatracker.ietf.org/doc/html/rfc1928
 */
public class SelectionMessage {
    public static class Methods{
        public static byte NO_AUTHENTICATION_REQUIRED = 0x00;
        public static byte GSSAPI = 0x01;
        public static byte USERNAME_PASSWORD = 0x02;
        public static byte NO_ACCEPTABLE_METHODS = (byte)0xff;
    }
    
    public static final byte[] DEFAULT_CLIENT_MSG = {
        Methods.NO_AUTHENTICATION_REQUIRED,
        // Methods.USERNAME_PASSWORD
    };

    public final int version = 5; // socks5
    public byte selectedMethod = -1;
    public byte[] methods;

    private SelectionMessage(){}

    public static SelectionMessage createClientMessage(){
        SelectionMessage msg = new SelectionMessage();
        msg.methods = DEFAULT_CLIENT_MSG;
        return msg;
    }
    public static SelectionMessage createClientMessage(byte[] methods){
        SelectionMessage msg = new SelectionMessage();
        msg.methods = methods;
        return msg;
    }
    public static SelectionMessage createServerMessage(byte selectedMethod){
        SelectionMessage msg = new SelectionMessage();
        msg.selectedMethod = selectedMethod;
        return msg;
    }

    public static void streamTo(OutputStream os, SelectionMessage msg) throws IOException{
        if(msg.methods == null){
            // server response
            os.write(msg.version);
            os.write(msg.selectedMethod);
            return;
        }
        os.write(msg.version);
        os.write(msg.methods.length);
        os.write(msg.methods);
    }

    public static SelectionMessage parse(InputStream is, boolean clientResponse) throws IOException{
        int ver = is.read();
        if(ver < 4) throw new IOException("Unsupported version: " + ver);
        if(ver == -1) throw new IOException("Connection is closed");
        SelectionMessage msg = new SelectionMessage();

        if(clientResponse){
            byte[] methods = new byte[is.read()];
            is.read(methods);
            msg.methods = methods;
            return msg;
        }
        msg.selectedMethod = (byte) is.read();
        return msg;
    }

    public String toString(){
        return String.format("SelectionMessage{ver: %d, meth: %d, methods: %s}", version, selectedMethod, methods == null? "null" : Arrays.toString(methods));
    }
}
