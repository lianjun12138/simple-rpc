package com.example.rpc.transport.client;

import com.example.rpc.protocol.MessageCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author LAJ
 * @date 2021-12-25 17:02:14
 */
public class RpcClient {
    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup group = new NioEventLoopGroup();
    private final String host;
    private final int port;
    
    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MessageCodec())
                                .addLast(new RpcClientHandler());
                    }
                });
    }
    
    public ChannelFuture connect() {
        // 连接指定的地址和端口
        ChannelFuture connect = bootstrap.connect(host, port);
        connect.awaitUninterruptibly();
        return connect;
    }
    
    public void close() {
        group.shutdownGracefully();
    }
}
