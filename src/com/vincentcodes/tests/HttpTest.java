package com.vincentcodes.tests;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.vincentcodes.net.Socks5Server;
import com.vincentcodes.net.message.SelectionMessage;
import com.vincentcodes.net.message.SocksRequest;

/**
 * Prepare your own webserver 127.0.0.1:1234
 */
public class HttpTest {
    public static void main(String[] args) throws IOException {
        new Thread(){
            @Override
            public void run() {
                Socket client;
                try {
                    byte[] buffer = new byte[4096];
                    client = new Socket("127.0.0.1", 1080);

                    OutputStream os = client.getOutputStream();
                    InputStream bis = new BufferedInputStream(client.getInputStream());

                    SelectionMessage message = SelectionMessage.createClientMessage();
                    SelectionMessage.streamTo(os, message);
                    SelectionMessage.parse(bis, false);

                    // connect to webserver 127.0.0.1:1234
                    SocksRequest request = new SocksRequest((byte)1, (byte)1, new byte[]{127, 0, 0, 1}, (short)1234);
                    SocksRequest.streamTo(os, request);
                    SocksRequest.parse(bis);

                    StringBuilder sb = new StringBuilder("GET / HTTP/1.1\r\n");
                    sb.append("Host: 127.0.0.1:1234\r\n");
                    sb.append("User-Agent: Mozilla/5.0\r\n\r\n");
                    os.write(sb.toString().getBytes());

                    int count = bis.read(buffer);
                    if(count != -1){
                        System.out.println("Webserver Reply: \n" + new String(buffer, 0, count));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        // Starts socks5 server on 1080.
        // Socks5Server.LOGGER.enable(LogType.DEBUG);
        new Socks5Server(1080).start();
    }
}
