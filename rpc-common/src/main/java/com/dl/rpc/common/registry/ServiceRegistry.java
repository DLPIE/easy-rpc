package com.dl.rpc.common.registry;

import java.net.InetSocketAddress;

// 服务注册中心
public interface ServiceRegistry {

    /**
     *
     * @param serviceName：服务名
     * @param inetSocketAddress：Server地址（ip+port）
     */
    public void register(String serviceName, InetSocketAddress inetSocketAddress);

}
