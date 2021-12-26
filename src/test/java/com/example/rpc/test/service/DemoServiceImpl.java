package com.example.rpc.test.service;

/**
 * @author LAJ
 * @date 2021-12-25 21:59:43
 */
public class DemoServiceImpl implements DemoService {
    public String sayHello(String param) {
        System.out.println("param" + param);
        return "hello:" + param;
    }
}
