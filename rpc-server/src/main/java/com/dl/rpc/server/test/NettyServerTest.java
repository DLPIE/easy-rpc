package com.dl.rpc.server.test;

import com.dl.api.HelloService;
import com.dl.rpc.server.DefaultServiceRegistry;
import com.dl.rpc.server.HelloServiceImpl;
import com.dl.rpc.server.ServiceProvider;
import com.dl.rpc.server.rpcServers.NettyServer;

import javax.imageio.spi.ServiceRegistry;

public class NettyServerTest {
    public static void main(String[] args) {
        // 创建注册表
        ServiceProvider serviceRegistry=new DefaultServiceRegistry();
        HelloService helloService = new HelloServiceImpl();
        serviceRegistry.register(helloService);

        // 创建server，监听9000端口
        NettyServer server = new NettyServer(serviceRegistry);
        server.listen(9999);
    }
}
