package com.dl.rpc.server.test;

import com.dl.api.HelloService;
import com.dl.rpc.server.provider.ServiceProviderImpl;
import com.dl.rpc.server.Impl.HelloServiceImpl;
import com.dl.rpc.server.provider.ServiceProvider;
import com.dl.rpc.server.rpcServers.SocketServer;

public class SocketServerTest {
    public static void main(String[] args) {
        // 创建注册表
        ServiceProvider serviceRegistry=new ServiceProviderImpl();
        HelloService helloService = new HelloServiceImpl();
        // serviceRegistry.register(helloService);

        // 创建server，监听9000端口
        SocketServer rpcServer = new SocketServer(serviceRegistry);
        rpcServer.listen(9000);
    }
}
