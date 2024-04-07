package com.dl.rpc.common.coder;

import com.dl.rpc.common.RpcRequest;
import com.dl.rpc.common.RpcResponse;
import com.dl.rpc.common.exception.RpcError;
import com.dl.rpc.common.exception.RpcException;
import com.dl.rpc.common.serialize.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 解码（入站处理器）
@Slf4j
public class CommonDecoder extends ReplayingDecoder {

    private static final int MAGIC_NUMBER=0xABCDABCD;
    /**
     * @param channelHandlerContext：代表pipeLine的一个Handler节点
     * @param byteBuf：堆外内存，从这里读取网络数据
     * @param list：将反序列化后的数据传递给下一个handler
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 1.校验魔数
        int magicNumber = byteBuf.readInt();
        if(magicNumber!=MAGIC_NUMBER){
            log.error("不识别的协议包：{}", magicNumber);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        // 2.取出数据包类型
        int packType = byteBuf.readInt();
        Class<?> targetClazz;
        if(packType==PackageType.REQUEST_PACK.getCode()){
            targetClazz= RpcRequest.class;
        }else if(packType==PackageType.RESPONSE_PACK.getCode()){
            targetClazz= RpcResponse.class;
        }else{
            log.error("不识别的数据包：{}", packType);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        // 3.取出序列化器
        int serializeCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializeCode);
        if(serializer==null){
            log.error("不识别的反序列化器：{}", serializeCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        // 4.取出数据长度
        int dataLen = byteBuf.readInt();
        // 5.取出数据
        byte[] data=new byte[dataLen];
        byteBuf.readBytes(data);
        // 反序列化，交给下个handler
        Object obj = serializer.deserialize(data, targetClazz);
        list.add(obj); // 将反序列化后的数据传递给下一个handler
    }
}
