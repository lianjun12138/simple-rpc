package com.example.rpc.proxy;

import com.example.rpc.Constants;
import com.example.rpc.protocol.Message;
import com.example.rpc.protocol.Request;
import com.example.rpc.protocol.Response;
import com.example.rpc.registry.Registry;
import com.example.rpc.registry.ServerInfo;
import com.example.rpc.transport.client.ChannelConnection;
import com.example.rpc.transport.client.RemoteResponsePromise;
import com.example.rpc.transport.client.RpcClient;
import io.netty.channel.ChannelFuture;
import org.apache.curator.x.discovery.ServiceInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.example.rpc.protocol.Message.*;

/**
 * @author LAJ
 * @date 2021-12-25 20:42:26
 */
public class RpcProxy implements InvocationHandler {
    
    private final String serviceName;
    
    private final Registry<ServerInfo> registry;
    
    public RpcProxy(String serviceName, Registry<ServerInfo> registry) {
        this.serviceName = serviceName;
        this.registry = registry;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 从Zookeeper缓存中获取可用的Server地址,并随机从中选择一个
        List<ServiceInstance<ServerInfo>> serviceInstances = registry.lookup(serviceName);
        if (serviceInstances.isEmpty()) {
            throw new RuntimeException("Get available serviceInstance error");
        }
        ServiceInstance<ServerInfo> serviceInstance =
                serviceInstances.get(ThreadLocalRandom.current().nextInt(serviceInstances.size()));
        // 创建请求消息
        Header header = new Header(Constants.MAGIC, Constants.VERSION_1);
        Request request = new Request(serviceName, method.getName(), args);
        Message<Request> requestMessage = new Message<>(header, request);
        // 向服务端发送请求, 进行远程调用
        Message<Response> responseMessage = remoteCall(serviceInstance.getPayload(), requestMessage);
        // 获取服务端响应
        Response response = responseMessage.getContent();
        return response.getResult();
    }
    
    /**
     * 远程调用
     *
     * @param serverInfo
     * @param requestMessage
     * @return
     */
    protected Message<Response> remoteCall(ServerInfo serverInfo, Message<Request> requestMessage) {
        if (serverInfo == null) {
            throw new RuntimeException("Get available server error");
        }
        
        Message<Response> responseMessage = null;
        try {
            // 创建Client连接指定的Server端
            RpcClient rpcClient = new RpcClient(serverInfo.getHost(), serverInfo.getPort());
            ChannelFuture future = rpcClient.connect().awaitUninterruptibly();
            // 创建对应的Connection对象，并发送请求
            ChannelConnection connection = new ChannelConnection(future);
            RemoteResponsePromise responsePromise = connection.request(requestMessage, Constants.DEFAULT_TIMEOUT);
            responseMessage = responsePromise.get(Constants.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseMessage;
    }
    
    public static <T> T newInstance(Registry<ServerInfo> registry, String serviceName, Class<T> clazz) {
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz}, new RpcProxy(serviceName, registry));
        return (T) proxy;
    }
}
