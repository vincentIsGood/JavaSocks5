package com.vincentcodes.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.vincentcodes.net.utils.ByteUtils;

/**
 * Reply is for socks5 server
 */
public class SocksReply {
    public static class StatusCodes{
        public static byte SUCCEEDED = 0x00;
        public static byte GENERAL_SERVER_FAILURE = 0x01;
        public static byte CONN_NOT_ALLOWED = 0x02;
        public static byte NETWORK_UNREACHABLE = 0x03;
        public static byte HOST_UNREACHABLE = 0x04;
        public static byte CONNECTION_REFUSED = 0x05;
        public static byte TTL_EXPIRED = 0x06;
        public static byte CMD_NOT_SUPPORTED = 0x07;
        public static byte ADDR_TYPE_NOT_SUPPORTED = 0x08;
    }
    
    public static class AddrType{
        public static byte IPv4 = 0x01;
        public static byte DOMAINNAME = 0x03;
        public static byte IPv6 = 0x04;
    }

    public final byte version = 5;
    public byte replyStatus;
    public byte addrType;
    public byte[] bindAddr;
    public short bindPort;

    public SocksReply(){}

    public SocksReply(byte replyStatus, byte addrType, byte[] bindAddr, short bindPort) {
        this.replyStatus = replyStatus;
        this.addrType = addrType;
        this.bindAddr = bindAddr;
        this.bindPort = bindPort;
    }

    public static void streamTo(OutputStream os, SocksReply reply) throws IOException{
        os.write(reply.version);
        os.write(reply.replyStatus);
        os.write(0x00); // reserved byte
        os.write(reply.addrType);
        if(reply.addrType == AddrType.DOMAINNAME)
            os.write(reply.bindAddr.length);
        os.write(reply.bindAddr);
        os.write(ByteUtils.shortToByteArray(reply.bindPort));
    }

    public static SocksReply parse(InputStream is) throws IOException{
        int ver = is.read();
        if(ver < 4) throw new IOException("Unsupported version: " + ver);
        if(ver == -1) throw new IOException("Connection is closed");
        SocksReply reply = new SocksReply();
        reply.replyStatus = (byte)is.read();
        is.read(); // skip reserved bit
        reply.addrType = (byte)is.read();

        if(reply.addrType == AddrType.IPv4) 
            reply.bindAddr = is.readNBytes(4);
        else if(reply.addrType == AddrType.IPv6) 
            reply.bindAddr = is.readNBytes(16);
        else if(reply.addrType == AddrType.DOMAINNAME) 
            reply.bindAddr = is.readNBytes(is.read());
        else throw new IOException("Unsupported address type: " + reply.addrType);

        reply.bindPort = (short) ByteUtils.getIntFrom2Bytes(is.readNBytes(2), 0);
        return reply;
    }

    public static String toString(SocksReply rep){
        return String.format(
            "SocketReply{v: %d, status: %d, addr: %s, bind: %s, port: %d}", 
            rep.version, rep.replyStatus, rep.addrType, Arrays.toString(rep.bindAddr), rep.bindPort
        );
    }
}
