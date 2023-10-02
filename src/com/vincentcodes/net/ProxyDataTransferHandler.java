package com.vincentcodes.net;

import java.io.IOException;

import com.vincentcodes.net.utils.IOContainer;

public interface ProxyDataTransferHandler {
    void takeover(IOContainer client, IOContainer endpoint) throws IOException;
}
