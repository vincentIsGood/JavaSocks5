package com.vincentcodes.net;

import com.vincentcodes.net.utils.IOContainer;

public class ConnectorResult {
    public final IOContainer socket;
    public final byte[] addr;
    public final short port;

    public ConnectorResult(IOContainer socket, byte[] addr, short port) {
        this.socket = socket;
        this.addr = addr;
        this.port = port;
    }
}
