package com.example.rpc.serialization;

/**
 * @author LAJ
 * @date 2021-12-25 14:29:17
 */
public class SerializationFactory {
    
    public static Serialization get(byte type) {
        switch (type & 0x7) {
            case 0x0:
            default:
                return new HessianSerialization();
        }
    }
}

