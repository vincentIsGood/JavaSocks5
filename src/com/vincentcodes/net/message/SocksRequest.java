package com.vincentcodes.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.vincentcodes.net.utils.ByteUtils;

/**
 * Request is for socks5 client
 */
public class SocksRequest {
    public static class Cmd{
        public static byte CONNECT = 0x01;
        public static byte BIND = 0x02;
        public static byte UDP_ASSOCIATE = 0x03;
    }

    public static class AddrType{
        public static byte IPv4 = 0x01;
        public static byte DOMAINNAME = 0x03;
        public static byte IPv6 = 0x04;
    }

    public final byte version = 5;
    public byte cmd;
    public byte addrType = AddrType.IPv4;
    public byte[] dstAddr; // variable length
    public short dstPort;

    public SocksRequest(){}

    public SocksRequest(byte cmd, byte addrType, byte[] dstAddr, short dstPort) {
        this.cmd = cmd;
        this.addrType = addrType;
        this.dstAddr = dstAddr;
        this.dstPort = dstPort;
    }

    public static void streamTo(OutputStream os, SocksRequest req) throws IOException{
        os.write(req.version);
        os.write(req.cmd);
        os.write(0x00); // reserved byte
        os.write(req.addrType);
        if(req.addrType == AddrType.DOMAINNAME)
            os.write(req.dstAddr.length);
        os.write(req.dstAddr);
        os.write(ByteUtils.shortToByteArray(req.dstPort));
    }

    public static SocksRequest parse(InputStream is) throws IOException{
        int ver = is.read();
        if(ver < 4) throw new IOException("Unsupported version: " + ver);
        if(ver == -1) throw new IOException("Connection is closed");
        SocksRequest req = new SocksRequest();
        req.cmd = (byte)is.read();
        is.read(); // skip reserved bit
        req.addrType = (byte)is.read();

        if(req.addrType == AddrType.IPv4) 
            req.dstAddr = is.readNBytes(4);
        else if(req.addrType == AddrType.IPv6) 
            req.dstAddr = is.readNBytes(16);
        else if(req.addrType == AddrType.DOMAINNAME) 
            req.dstAddr = is.readNBytes(is.read());
        else throw new IOException("Unsupported address type: " + req.addrType);

        req.dstPort = (short) ByteUtils.getIntFrom2Bytes(is.readNBytes(2), 0);
        return req;
    }

    public static String toString(SocksRequest req){
        return String.format(
            "SocketRequest{v: %d, cmd: %d, addr: %s, dst: %s, port: %d}", 
            req.version, req.cmd, req.addrType, Arrays.toString(req.dstAddr), req.dstPort
        );
    }
}
