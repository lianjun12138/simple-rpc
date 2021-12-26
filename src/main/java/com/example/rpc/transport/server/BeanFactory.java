package com.example.rpc.transport.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LAJ
 * @date 2021-12-25 15:31:28
 */
public class BeanFactory {
    private static final Map<String, Object> services =  new ConcurrentHashMap<>();
    
    public static void register(String serviceName, Object bean) {
        services.put(serviceName, bean);
    }
    
    public static Object get(String serviceName) {
        return services.get(serviceName);
    }
}
