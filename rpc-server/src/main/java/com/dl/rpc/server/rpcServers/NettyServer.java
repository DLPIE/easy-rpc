package com.dl.rpc.server.rpcServers;

import com.dl.rpc.common.coder.CommonDecoder;
import com.dl.rpc.common.coder.CommonEncoder;
import com.dl.rpc.common.exception.RpcError;
import com.dl.rpc.common.exception.RpcException;
import com.dl.rpc.common.hook.ShutdownHook;
import com.dl.rpc.common.serialize.CommonSerializer;
import com.dl.rpc.server.provider.ServiceProvider;
import com.dl.rpc.server.provider.ServiceProviderImpl;
import com.dl.rpc.common.registry.NacosServiceRegistry;
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
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyServer extends AbstractRpcServer{
    CommonSerializer serializer;


    public NettyServer(String host,int port) {
        serviceProvider=new ServiceProviderImpl();
        serviceRegistry=new NacosServiceRegistry();
        this.host=host;
        this.port=port;
        scanServices(); // 扫描，实现服务自动注册
    }


    @Override
    public void listen(int port) {
        if(serializer==null){
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
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
                            pipeline.channel().attr(key).set(serviceProvider);
                            // 添加handler todo 顺序
                            pipeline.addLast(new CommonDecoder()) // 解码
                                    .addLast(new CommonEncoder(serializer)) // 编码（出站）
                                    .addLast(new NettyServerHandler()); // 业务：处理request，返回response
                        }
                    });
            // 启动netty，监听指定端口
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 添加钩子，在jvm突然关闭时自动注销服务
            ShutdownHook.getShutdownHook().addClearAllHook();
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

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer=serializer;
    }
}
