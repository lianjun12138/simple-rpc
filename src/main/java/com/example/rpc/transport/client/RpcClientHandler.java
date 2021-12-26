package com.example.rpc.transport.client;

import com.example.rpc.protocol.Response;
import com.example.rpc.protocol.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author LAJ
 * @date 2021-12-25 15:42:57
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<Message<Response>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message<Response> msg) throws Exception {
        Long messageId = msg.getHeader().getMessageId();
        RemoteResponsePromise promise = ChannelConnection.PENDING_PROMISE_MAP.remove(messageId);
        promise.setSuccess(msg);
    }
}
