package com.dl.rpc.server.test;

import com.dl.api.HelloService;
import com.dl.rpc.common.serialize.JsonSerializer;
import com.dl.rpc.server.provider.ServiceProviderImpl;
import com.dl.rpc.server.HelloServiceImpl;
import com.dl.rpc.server.provider.ServiceProvider;
import com.dl.rpc.server.rpcServers.NettyServer;

public class NettyServerTest {
    public static void main(String[] args) {
        // 提供的服务
        HelloService helloService = new HelloServiceImpl();
        // 注册服务到注册表和 Nacos
        NettyServer server = new NettyServer("127.0.0.1",9999);
        server.publishService(helloService,HelloService.class);
        server.setSerializer(new JsonSerializer());
        // 启动Netty Server
        server.listen(9999);
    }
}
