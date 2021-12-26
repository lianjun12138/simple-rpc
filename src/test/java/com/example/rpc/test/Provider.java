package com.example.rpc.test;

import com.example.rpc.registry.ServerInfo;
import com.example.rpc.registry.ZookeeperRegistry;
import com.example.rpc.test.service.DemoServiceImpl;
import com.example.rpc.transport.server.BeanFactory;
import com.example.rpc.transport.server.RpcServer;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @author LAJ
 * @date 2021-12-25 21:58:37
 */
public class Provider {
    public static void main(String[] args) throws Exception {
        // 创建DemoServiceImpl，并注册到BeanManager中
        BeanFactory.register("demoService", new DemoServiceImpl());
        // 创建ZookeeperRegistry，并将Provider的地址信息封装成ServerInfo
        ZookeeperRegistry discovery = new ZookeeperRegistry("localhost:2181");
        discovery.start();
        // 对象注册到Zookeeper
        ServerInfo serverInfo = new ServerInfo("127.0.0.1", 20880);
        ServiceInstance<ServerInfo> serviceInstance = ServiceInstance.<ServerInfo>builder()
                .name("demoService")
                .payload(serverInfo)
                .build();
        discovery.register(serviceInstance);
        
        // 启动DemoRpcServer，等待Client的请求
        RpcServer rpcServer = new RpcServer(20880);
        rpcServer.start();
        while (!Thread.currentThread().isInterrupted()) {
            Thread.sleep(1000);
        }
    }
}
