package com.dl.rpc.server;

import com.dl.rpc.common.RpcRequest;
import com.dl.rpc.common.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

// 服务端-入站处理器：处理的是RpcRequest
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> { // 泛型很关键，处理RpcRequest类型的数据

    private static ServiceProvider serviceRegistry;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        try {
            log.info("服务器收到请求:{}",rpcRequest);
            // 从管道中取出注册表（之前bootStrap初始化已经放入channal）
            AttributeKey<ServiceProvider> key = AttributeKey.valueOf("serviceProvider");// 根据名称获取AttributeKey实例
            serviceRegistry = ctx.channel().attr(key).get(); // 从AttributeMap，根据AttributeKey获取value
            // 从注册表找到service
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            // 寻找方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object methodResult = method.invoke(service, rpcRequest.getParamValues());
            // 将结果封装为RpcResponse，写入channal并刷到网络（会触发出站处理器，即编码）
            ChannelFuture channelFuture = ctx.writeAndFlush(RpcResponse.success(methodResult)); // 异步操作，所以返回一个future对象
            // 上一步操作完成时，触发回调关闭channel -> 然后NettyServer才会停止阻塞（58行）
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(rpcRequest); // 释放资源，避免内存泄漏
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
