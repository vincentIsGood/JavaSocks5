package com.vincentcodes.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.vincentcodes.logger.Logger;

public class Socks5Server {
    public static final Logger LOGGER = new Logger();
    
    private ExecutorService executorService = Executors.newFixedThreadPool(16);
    private boolean closeRequested = false;

    private final int port;
    private final InetAddress bindAddr;

    public Socks5Server(int port){
        this(port, null);
    }
    public Socks5Server(int port, InetAddress bindAddr){
        this.port = port;
        this.bindAddr = bindAddr;
    }

    public void start() throws IOException{
        LOGGER.info("Server is running on port " + port);
        // TODO: currently the server operates on unencrypted environment
        try (ServerSocket serverSocket = new ServerSocket(port, 50, bindAddr)) {
            while(!closeRequested){
                executorService.submit(new Socks5Connection(serverSocket.accept()));
            }
        }
    }

    public void close(){
        closeRequested = true;
        shutdownAndAwaitTermination(executorService);
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
            pool.shutdownNow();
            if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                LOGGER.err("ExecutorService did not terminate");
            }
        } catch (InterruptedException ex) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
