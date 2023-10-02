package com.vincentcodes.net.defaults;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.vincentcodes.net.ProxyDataTransferHandler;
import com.vincentcodes.net.utils.IOContainer;
import com.vincentcodes.net.utils.IOUtils;

public class ProxyDataTransferHandlerImpl implements ProxyDataTransferHandler {

    @Override
    public void takeover(IOContainer client, IOContainer endpoint) throws IOException {
        Thread endpointToClientThread = new Thread("endpointToClientThread"){
            @Override
            public void run() {
                try {
                    System.out.println("[+] Starting endpoint to client thread: " + Thread.currentThread().getName());
                    IOUtils.readFromAsendToB(endpoint, client);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new UncheckedIOException(e);
                }
                return;
            }
        };
        endpointToClientThread.start();

        IOUtils.readFromAsendToB(client, endpoint);
    }
}
