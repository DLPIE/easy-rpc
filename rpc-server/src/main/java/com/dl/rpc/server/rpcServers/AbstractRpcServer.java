package com.dl.rpc.server.rpcServers;

import com.dl.rpc.common.annotation.Service;
import com.dl.rpc.common.annotation.ServiceScan;
import com.dl.rpc.common.exception.RpcError;
import com.dl.rpc.common.exception.RpcException;
import com.dl.rpc.common.registry.ServiceRegistry;
import com.dl.rpc.common.util.ReflectUtil;
import com.dl.rpc.server.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Set;

import static java.lang.Class.forName;

@Slf4j
public abstract class AbstractRpcServer implements RpcServer{

    String host;
    int port;
    ServiceProvider serviceProvider; // 此对象用来注册服务到注册表

    ServiceRegistry serviceRegistry; // 此对象用来注册服务到Nacos


    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.register(service,serviceName); // 注册到注册表
        serviceRegistry.register(serviceName,new InetSocketAddress(host,port)); // com.dl.HelloService注册到nacos
    }

    /**
     * 扫描服务，这个方法用于扫描所有指定包下的类，然后将那些添加了Service注解的类作为service进行publish。
     */
    public void scanServices() {
        // 1.通过调用栈来获取main方法的类名。
        String mainClassName = ReflectUtil.getStackTrace();
        // 2.获取class对象
        Class<?> mainClass;
        try {
             mainClass = Class.forName(mainClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // 3.检查有无扫描注解
        if(!mainClass.isAnnotationPresent(ServiceScan.class)){
            log.error("启动类缺少@ServiceScan注解");
            throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
        }
        // 4.获取扫描注解的值
        String basePackage = mainClass.getAnnotation(ServiceScan.class).value();

        // 5.获取这个包下的所有类的class对象(复杂，别人写的)
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        // 6.遍历(iter+回车)，如果有Service注解就注册到Nacos和注册表
        for (Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(Service.class)){
                // service对象
                Object service;
                try {
                    service = clazz.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                // serviceName根据注解的value，分2类讨论
                String serviceName = clazz.getAnnotation(Service.class).name();
                if(!serviceName.equals("")){ // 用户输入了serviceName
                    publishService(service,serviceName);
                }else{
                    // 遍历实现的接口，进行注册
                    for (Class<?> oneInterface : clazz.getInterfaces()) {
                        publishService(service,oneInterface.getCanonicalName());
                    }
                }
            }
        }
    }
}
