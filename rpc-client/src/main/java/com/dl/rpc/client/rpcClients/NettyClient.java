package com.dl.rpc.client.rpcClients;

import com.dl.rpc.common.RpcRequest;
import com.dl.rpc.common.RpcResponse;
import com.dl.rpc.common.coder.CommonDecoder;
import com.dl.rpc.common.coder.CommonEncoder;
import com.dl.rpc.common.serialize.CommonSerializer;
import com.dl.rpc.common.serialize.JsonSerializer;
import com.dl.rpc.server.registry.NacosServiceRegistry;
import com.dl.rpc.server.registry.ServiceRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;


@Slf4j
public class NettyClient implements RpcClient{

    private static final Bootstrap bootstrap; // netty启动器

    private CommonSerializer serializer;

    private ServiceRegistry serviceRegistry; // 利用Nacos服务发现，懒加载

    public NettyClient() {
        this.serviceRegistry=new NacosServiceRegistry();
    }

    // 初始化bootstrap。不同于Server端，它放到了静态代码块里，这样就避免了每次send都要重新初始化
    static {
        // 模板套路代码
        EventLoopGroup group = new NioEventLoopGroup(2); // 空参构造器，默认会创建CPU*2个EventLoop，即8个
        bootstrap=new Bootstrap()
                .group(group) // 1.设置EventLoopGroup
                .channel(NioSocketChannel.class) // 2.指定Channel类型
                .option(ChannelOption.SO_KEEPALIVE, true) // 若TCP连接长时间没有数据交换，会发送一个“keep-alive”探测包来检查连接是否仍然有效。没响应就断开
                .handler(new ChannelInitializer<SocketChannel>() { // 3.设置handler
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        // 注意顺序：依次添加到pipeline的末尾
                        pipeline.addLast(new CommonDecoder()) // 解码
                                .addLast(new CommonEncoder(new JsonSerializer())) // 编码
                                .addLast(new NettyClientHandler()); // 业务：解析response，加入k-v

                    }
                });
    }

    @Override
    public Object sendRequest(RpcRequest request) {
        try {
            // 多一步：利用nacos服务发现，获取目标ip和port(***)
            InetSocketAddress inetSocketAddress = serviceRegistry.findService(request.getInterfaceName());
            String host=inetSocketAddress.getHostName();
            int port=inetSocketAddress.getPort();
            // 1.client阻塞，直到与服务端创建了连接（connect是nio线程异步执行的，所以用sync阻塞主线程，直到connect完毕）
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            // 2.获取channal
            Channel channel = channelFuture.channel();
            // 3.发送rpcRequest到服务端(写入channal、flush刷到网络)
            if(channel!=null){
                channel.writeAndFlush(request).addListener(future -> { // 在发送完毕时回调，告知是否发送成功
                    if(future.isSuccess()){
                        System.out.println("成功发送数据:"+request.toString());
                    }else{
                        System.out.println("发送消息时发生错误:" + future.cause());
                    }
                });
            }
            // 4.client阻塞，直到channel关闭
            channel.closeFuture().sync();
            // 5.从channal绑定的map中取出RpcResponse
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            RpcResponse rpcResponse = channel.attr(key).get();
            return rpcResponse.getData();
        } catch (InterruptedException e) {
            log.error("发送消息时发生错误：", e);
        }
        return null;
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer=serializer;
    }
}
