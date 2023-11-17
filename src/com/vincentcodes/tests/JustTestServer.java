package com.vincentcodes.tests;

import java.io.IOException;

import com.vincentcodes.net.Socks5Server;

public class JustTestServer {
    public static void main(String[] args) throws IOException {
        // Starts socks5 server on 1080.
        // Socks5Server.LOGGER.enable(LogType.DEBUG);
        new Socks5Server(1080).start();
    }
}
