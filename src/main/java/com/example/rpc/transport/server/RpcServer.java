package com.example.rpc.transport.server;

import com.example.rpc.protocol.MessageCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author LAJ
 * @date 2021-12-25 17:12:11
 */
public class RpcServer {
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workGroup = new NioEventLoopGroup();
    private Channel channel;
    private final int port;
    
    public RpcServer(int port) {
        this.port = port;
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MessageCodec())
                                .addLast(new RpcServerHandler());
                    }
                });
    }
    
    public ChannelFuture start() throws InterruptedException {
        // 监听指定的端口
        ChannelFuture channelFuture = serverBootstrap.bind(port);
        channel = channelFuture.channel();
        channel.closeFuture();
        return channelFuture;
    }
    
    public void shutdown() throws InterruptedException {
        channel.close().sync();
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
