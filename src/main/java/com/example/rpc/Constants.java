package com.example.rpc;

/**
 * @author LAJ
 * @date 2021-12-25 14:38:31
 */
public class Constants {
    
    public static final int HEADER_SIZE = 16;
    
    public static final short MAGIC = (short) 0xE0F1;
    
    public static final byte VERSION_1 = 1;
    
    public static final int DEFAULT_TIMEOUT = 500000;
    
    public static boolean isHeartBeat(byte extraInfo) {
        return (extraInfo & 32) != 0;
    }
    
    public static boolean isRequest(byte extraInfo) {
        return (extraInfo & 1) != 1;
    }
}
