package com.vincentcodes.net.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Used to store IO Streams, especially when you have
 * upgraded the IO into buffered IO which means you
 * need to retain the buffered data and access it without
 * the data getting lost
 */
public class IOContainer {
    private Socket socket;
    private InputStream is;
    private OutputStream os;

    public IOContainer(Socket socket, InputStream is, OutputStream os){
        this.socket = socket;
        this.is = is;
        this.os = os;
    }

    public Socket getSocket(){
        return socket;
    }

    public InputStream getInputStream(){
        return is;
    }
    
    public OutputStream getOutputStream(){
        return os;
    }
}
