package com.example.rpc.transport.server;

import com.example.rpc.protocol.Message;
import com.example.rpc.protocol.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author LAJ
 * @date 2021-12-25 15:12:24
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<Message<Request>> {
    static Executor executor = Executors.newCachedThreadPool();
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message<Request> msg) throws Exception {
        // 心跳消息, 直接返回
        if (msg.isHeartBeat()) {
            ctx.writeAndFlush(msg);
            return;
        }
        executor.execute(new LocalInvocationRunnable(msg, ctx));
    }
}
