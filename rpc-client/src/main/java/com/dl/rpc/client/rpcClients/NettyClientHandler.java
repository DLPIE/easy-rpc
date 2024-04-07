package com.dl.rpc.client.rpcClients;

import com.dl.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

// 客户端-入站处理器：处理的是RpcResponse
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        try {
            log.info("客户端收到响应{}",rpcResponse);
            // 将rpcResponse以k-v的形式存入channel
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse"); // 创建Key
            ctx.channel().attr(key).set(rpcResponse); // set(key,rpcResponse)
            ctx.channel().close(); // 业务handler是最后一站了，可以关闭channel了 -> 然后NettyClient才会停止阻塞(69行)
        } finally {
            ReferenceCountUtil.release(rpcResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
