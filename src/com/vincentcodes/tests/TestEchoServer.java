package com.vincentcodes.tests;

import java.io.IOException;

import com.vincentcodes.net.Socks5Server;

public class TestEchoServer {
    public static void main(String[] args) throws IOException {
        new Socks5Server(1080).start();
    }
}
