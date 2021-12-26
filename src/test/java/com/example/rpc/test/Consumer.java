package com.example.rpc.test;

import com.example.rpc.proxy.RpcProxy;
import com.example.rpc.registry.ZookeeperRegistry;
import com.example.rpc.test.service.DemoService;

/**
 * @author LAJ
 * @date 2021-12-25 21:58:54
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        ZookeeperRegistry discovery = new ZookeeperRegistry("localhost:2181");
        discovery.start();
        // 创建代理对象，通过代理调用Client对象请求Server以获取响应
        DemoService demoService = RpcProxy.newInstance(discovery, "demoService", DemoService.class);
        // 调用sayHello()方法，并输出结果
        String result = demoService.sayHello("mahaifeng");
        System.out.println(result);
    }
}
