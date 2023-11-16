package com.vincentcodes.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.vincentcodes.net.Socks5Server;

public class TestEchoServer {
    public static void main(String[] args) throws IOException {
        // Start echo server to simulate a remote server.
        Thread echoServerEndpoint = new Thread(){
            public void run() {
                System.out.println("[+] Starting echo server on port 1234");
                try (ServerSocket server = new ServerSocket(1234)) {
                    Socket client;
                    while((client = server.accept()) != null){
                        new EchoServerHandler(client).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        };
        echoServerEndpoint.start();
        
        // Starts socks5 server on 1080.
        new Socks5Server(1080).start();
    }

    static class EchoServerHandler extends Thread{
        private Socket client;

        public EchoServerHandler(Socket client) throws IOException{
            this.client = client;
        }

        @Override
        public void run() {
            System.out.println("[+] Client connected: " + client.getRemoteSocketAddress());
            try {
                InputStream is = client.getInputStream();
                OutputStream os = client.getOutputStream();

                byte[] buffer = new byte[4096];
                int count;
                while((count = is.read(buffer)) != -1){
                    System.out.println("Echo count: " + count);
                    os.write(buffer, 0, count);
                    os.flush();
                }
                System.out.println("[+] Writing bytes back to client");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
    }
}
