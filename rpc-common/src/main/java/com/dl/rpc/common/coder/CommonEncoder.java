package com.dl.rpc.common.coder;

import com.dl.rpc.common.RpcRequest;
import com.dl.rpc.common.serialize.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

// 编码器（出站处理器）
public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER=0xABCDABCD;

    CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer){ // 传入序列化器
        this.serializer=serializer;
    }

    /**
     * 要实现的方法
     * @param channelHandlerContext：代表pipeLine的一个Handler节点
     * @param msg：待编码的消息，RpcRequest/RPCResponse
     * @param byteBuf：堆外内存，写数据到这里，通过网络发出去。
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
        // 1.魔数：表示一个协议包，用来识别我们自定义的协议。
        byteBuf.writeInt(MAGIC_NUMBER);
        // 2.包类型：用来标识这是一个Request还是Response
        if(msg instanceof RpcRequest){
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        }else{
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        // 3.序列化器：Server和Client应统一
        byteBuf.writeInt(SerializerCode.JSON.getCode());
        // 4.数据长度：防止粘包
        byte[] bytes = serializer.serialize(msg);
        byteBuf.writeInt(bytes.length);
        // 5.数据：被序列化后的字节流
        byteBuf.writeBytes(bytes);
    }
}
