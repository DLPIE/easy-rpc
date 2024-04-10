package com.dl.rpc.common.registry;

import com.dl.rpc.common.exception.RpcError;
import com.dl.rpc.common.exception.RpcException;
import com.dl.rpc.common.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NacosServiceRegistry implements ServiceRegistry{

    // 注册：调用namingService的registerInstance(服务名，hostName,port)方法
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.register(serviceName,inetSocketAddress);
        } catch (Exception e) {
            log.error("Nacos注册时发生错误:",e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}
