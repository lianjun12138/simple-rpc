package com.example.rpc.transport.server;

import com.example.rpc.protocol.Request;
import com.example.rpc.protocol.Response;
import com.example.rpc.protocol.Message;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;

import static com.example.rpc.protocol.Message.Header;


/**
 * @author LAJ
 * @date 2021-12-25 15:24:40
 */
public class LocalInvocationRunnable implements Runnable {
    private final Message<Request> message;
    private final ChannelHandlerContext ctx;
    
    public LocalInvocationRunnable(Message<Request> message, ChannelHandlerContext ctx) {
        this.message = message;
        this.ctx = ctx;
    }
    
    @Override
    public void run() {
        Request request = message.getContent();
        Response response = localInvoke(request);
        
        Header header = message.getHeader();
        header.setExtraInfo((byte) 1);
        
        ctx.writeAndFlush(new Message<>(header, response));
    }
    
    private Response localInvoke(Request request) {
        String methodName = request.getMethodName();
        Class<?>[] argTypes = request.getArgTypes();
        Object[] args = request.getArgs();
        
        String serviceName = request.getServiceName();
        Object service = BeanFactory.get(serviceName);
        
        Response response = new Response();
        try {
            // 通过反射调用Service方法
            Method method = service.getClass().getMethod(methodName, argTypes);
            Object result = method.invoke(service, args);
            response.setResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
