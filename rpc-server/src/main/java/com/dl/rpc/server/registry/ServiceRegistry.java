package com.dl.rpc.server.registry;

import java.net.InetSocketAddress;

// 服务注册中心
public interface ServiceRegistry {

    public void register(String serviceName, InetSocketAddress inetSocketAddress);

    InetSocketAddress findService(String serviceName);
}
