package com.dl.rpc.server.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.dl.rpc.common.exception.RpcError;
import com.dl.rpc.common.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServiceRegistry implements ServiceRegistry{
    private  static NamingService namingService;
    private static String SERVER_ADDR="127.0.0.1:8848";

    // 连接Nacos，获取核心对象namingService
    static {
        try {
            namingService=NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            log.error("Nacos连接时发生错误:",e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    // 注册：调用namingService的registerInstance(服务名，hostName,port)方法
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        } catch (NacosException e) {
            log.error("Nacos注册时发生错误:",e);
            throw new RuntimeException(e);
        }
    }

    // 服务发现：调用namingService的getAllInstances方法
    @Override
    public InetSocketAddress findService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            Instance instance = instances.get(0);// 负载均衡 todo
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        } catch (NacosException e) {
            log.error("Nacos获取服务时发生错误:",e);
            throw new RuntimeException(e);
        }
    }
}
