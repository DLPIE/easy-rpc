package com.dl.rpc.server.rpcServers;

import com.dl.rpc.common.coder.CommonDecoder;
import com.dl.rpc.common.coder.CommonEncoder;
import com.dl.rpc.common.serialize.JsonSerializer;
import com.dl.rpc.server.NettyServerHandler;
import com.dl.rpc.server.ServiceProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import org.apache.catalina.startup.Bootstrap;

public class NettyServer implements RpcServer{

    private ServiceProvider serviceRegistry;

    public NettyServer(ServiceProvider serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void listen(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(2); // 处理客户端连接
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2); // 处理客户端IO

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)) // 日志handler
                    .option(ChannelOption.SO_BACKLOG, 256) // 存放等待接受的网络连接的队列长度
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true) // 禁用Nagle算法
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 往channel中添加注册表<AttributeKey,注册表>
                            AttributeKey<ServiceProvider> key = AttributeKey.valueOf("serviceProvider");
                            pipeline.channel().attr(key).set(serviceRegistry);
                            // 添加handler todo 顺序
                            pipeline.addLast(new CommonDecoder()) // 解码
                                    .addLast(new CommonEncoder(new JsonSerializer())) // 编码（出站）
                                    .addLast(new NettyServerHandler()); // 业务：处理request，返回response
                        }
                    });
            // 启动netty，监听指定端口
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // server一直阻塞，直到channel关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 最后关闭group
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
