package com.example.rpc.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LAJ
 * @date 2021-12-25 19:52:29
 */
public class ZookeeperRegistry implements Registry<ServerInfo> {
    
    private ServiceDiscovery<ServerInfo> serviceDiscovery;
    
    private ServiceCache<ServerInfo> serviceCache;
    
    private String address;
    
    public ZookeeperRegistry(String address) {
        this.address = address;
    }
    
    public void start() throws Exception {
        String root = "/rpc";
        CuratorFramework client = CuratorFrameworkFactory.newClient(address,
                new ExponentialBackoffRetry(1000, 3));
        client.start();
        client.blockUntilConnected();  // 阻塞当前线程，等待连接成功
        client.createContainers(root);
    
        // 启动ServiceDiscovery
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServerInfo.class)
                .client(client).basePath(root)
                .serializer(new JsonInstanceSerializer<>(ServerInfo.class))
                .build();
        serviceDiscovery.start();
        
        // 启动ServiceCache
//        serviceCache = serviceDiscovery.serviceCacheBuilder()
//                .name("/demoService")
//                .build();
//
//        serviceCache.start();
    
    }
    
    @Override
    public void register(ServiceInstance<ServerInfo> service) throws Exception {
        serviceDiscovery.registerService(service);
    }
    
    @Override
    public void unregister(ServiceInstance<ServerInfo> service) throws Exception {
        serviceDiscovery.unregisterService(service);
    }
    
    @Override
    public List<ServiceInstance<ServerInfo>> lookup(String serviceName) throws Exception {
        return new ArrayList<>(serviceDiscovery.queryForInstances(serviceName));
    }
}
