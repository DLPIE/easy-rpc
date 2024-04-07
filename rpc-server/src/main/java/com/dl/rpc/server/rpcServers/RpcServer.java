package com.dl.rpc.server.rpcServers;

import com.dl.rpc.common.serialize.CommonSerializer;

public interface RpcServer {
    /**
     * 监听端口，启动server
     * @param port
     */
    void listen(int port);

    /**
     * 向Nacos注册服务
     * @param servie
     * @param serviceClass
     * @param <T>
     */
    <T> void publishService(Object servie,Class<T> serviceClass);

    /**
     * 设置server的序列化器
     * @param serializer
     */
    void setSerializer(CommonSerializer serializer);
}
