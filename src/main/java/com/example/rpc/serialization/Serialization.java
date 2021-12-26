package com.example.rpc.serialization;

import java.io.IOException;

/**
 * @author LAJ
 * @date 2021-12-25 14:10:37
 */
public interface Serialization {
    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> byte[] serialize(T obj) throws IOException;
    
    /**
     * 反序列化
     * @param data
     * @param clz
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}
