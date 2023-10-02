package com.vincentcodes.net.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @see https://datatracker.ietf.org/doc/rfc1929/
 */
public class UserPassMessage {
    public static final byte version = 1;
    public String username;
    public String password;

    public UserPassMessage(){}

    public UserPassMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static void serverReply(OutputStream os, boolean success) throws IOException{
        os.write(UserPassMessage.version);
        os.write(success? 0 : 1);
    }

    public static void streamTo(OutputStream os, UserPassMessage msg) throws IOException{
        os.write(UserPassMessage.version);
        os.write(msg.username.length());
        os.write(msg.username.getBytes());
        os.write(msg.password.length());
        os.write(msg.password.getBytes());
    }

    public static UserPassMessage parse(InputStream is) throws IOException{
        int ver = is.read();
        if(ver != UserPassMessage.version) throw new IOException("Unsupported user-pass auth version: " + ver);
        if(ver == -1) throw new IOException("Connection is closed");
        UserPassMessage msg = new UserPassMessage();
        msg.username = new String(is.readNBytes(is.read()));
        msg.password = new String(is.readNBytes(is.read()));
        return msg;
    }

    public static String toString(UserPassMessage msg){
        return String.format("UserPassMessage{ver: %d, user: %s, pass: %s}", UserPassMessage.version, msg.username, msg.password);
    }
}
