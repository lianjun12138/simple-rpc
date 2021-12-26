package com.example.rpc.transport.client;

import com.example.rpc.protocol.Request;
import com.example.rpc.protocol.Message;
import io.netty.channel.ChannelFuture;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author LAJ
 * @date 2021-12-25 16:13:03
 */
public class ChannelConnection implements Closeable {
    /**
     * 用于生成消息ID，全局唯一
     */
    private final static AtomicLong ID_GENERATOR = new AtomicLong(0);
    
    public final static Map<Long, RemoteResponsePromise> PENDING_PROMISE_MAP = new ConcurrentHashMap<>();
    
    private final ChannelFuture future;
    
    private final AtomicBoolean isConnected = new AtomicBoolean();
    
    public ChannelConnection(ChannelFuture future) {
        this.future = future;
        this.isConnected.set(true);
    }
    
    public ChannelConnection(ChannelFuture future, boolean isConnected) {
        this.future = future;
        this.isConnected.set(isConnected);
    }
    
    public RemoteResponsePromise request(Message<Request> requestMessage, long timeOut) {
        // 生成并设置消息ID
        long messageId = ID_GENERATOR.incrementAndGet();
        requestMessage.getHeader().setMessageId(messageId);
        
        // 创建消息相关的Promise
        RemoteResponsePromise responsePromise = new RemoteResponsePromise(requestMessage);
        PENDING_PROMISE_MAP.put(messageId, responsePromise);
        
        // 发送请求
        future.channel().writeAndFlush(requestMessage);
        return responsePromise;
    }
    
    @Override
    public void close() throws IOException {
        future.channel().close();
    }
}
