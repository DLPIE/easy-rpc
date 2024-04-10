package com.dl.rpc.server.test;

import com.dl.api.HelloService;
import com.dl.rpc.common.annotation.ServiceScan;
import com.dl.rpc.common.serialize.JsonSerializer;
import com.dl.rpc.server.Impl.HelloServiceImpl;
import com.dl.rpc.server.rpcServers.NettyServer;

@ServiceScan("com.dl.rpc.server.Impl") // 扫描这个路径下的类，含有@Service就注册到Nacos
public class NettyServerTest {
    public static void main(String[] args) {
        // 提供的服务
        HelloService helloService = new HelloServiceImpl();
        // 注册服务到注册表和 Nacos
        NettyServer server = new NettyServer("127.0.0.1",9999);
        server.setSerializer(new JsonSerializer());
        // 启动Netty Server
        server.listen(9999);
    }
}
