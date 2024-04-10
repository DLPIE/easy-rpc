package com.dl.rpc.common.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.dl.rpc.common.exception.RpcError;
import com.dl.rpc.common.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 每台服务器一个Nacos工具类
 */
@Slf4j
public class NacosUtil {
    private  static NamingService namingService;
    private static String SERVER_ADDR="127.0.0.1:8848"; // 部署Nacos的地址
    static InetSocketAddress address; // 当前Server地址
    static Set<String> serviceNames=new HashSet<>(); // 当前Server注册的服务

    // 1.连接Nacos，获取核心对象namingService
    static {
        try {
            namingService= NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            log.error("Nacos连接时发生错误:",e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    // 2.服务注册：调用namingService的registerInstance(服务名，hostName,port)方法
    public static void register(String serviceName, InetSocketAddress address) throws Exception{
        namingService.registerInstance(serviceName,address.getHostName(),address.getPort());
        serviceNames.add(serviceName);
        NacosUtil.address=address;
    }

    // 3.服务发现
    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    // 4.服务注销
    public static void deRegisterService(){
        if(address!=null && !serviceNames.isEmpty()){
            for(String serviceName:serviceNames){
                try {
                    // 依然3个参数
                    namingService.deregisterInstance(serviceName,address.getHostName(),address.getPort());
                } catch (NacosException e) {
                    log.error("注销服务{}失败",serviceName,e);
                }
            }
        }
    }
}
