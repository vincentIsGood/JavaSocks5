package com.vincentcodes.net;

import java.io.IOException;

import com.vincentcodes.net.utils.IOContainer;

public interface ProxyTunnelHandler {
    /**
     * @param endpoint refers to the remote server
     */
    void takeover(IOContainer client, IOContainer endpoint) throws IOException;
}
