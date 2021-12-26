package com.example.rpc.registry;

import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;

/**
 * @author LAJ
 * @date 2021-12-25 17:33:26
 */
public interface Registry<T> {
    
    void register(ServiceInstance<T> service) throws Exception;
    
    void unregister(ServiceInstance<T> service) throws Exception;
    
    List<ServiceInstance<T>> lookup(String serviceName) throws Exception;
}
