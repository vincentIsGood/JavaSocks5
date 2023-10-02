package com.vincentcodes.net.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    /**
     * [blocking] read until end of stream
     */
    public static void readFromAsendToB(IOContainer a, IOContainer b) throws IOException{
        InputStream is = a.getInputStream();
        OutputStream os = b.getOutputStream();
        readFromAsendToB(is, os);
    }
    public static void readFromAsendToB(InputStream is, OutputStream os) throws IOException{
        byte[] buffer = new byte[4096];
        int count;
        while((count = is.read(buffer)) != -1){
            os.write(buffer, 0, count);
            os.flush();
        }
    }
}
