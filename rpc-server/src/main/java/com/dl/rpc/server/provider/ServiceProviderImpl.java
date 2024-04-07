package com.dl.rpc.server.provider;

import com.dl.rpc.common.exception.RpcError;
import com.dl.rpc.common.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ServiceProviderImpl implements ServiceProvider {
    Map<String,Object> serviceMap=new HashMap<>();
    Set<String> registeredService=new HashSet<>();

    public Map<String, Object> getServiceMap() {
        return serviceMap;
    }

    /**
     * 注册服务到注册表，注意syn保证线程安全
     * @param service
     * @param <T>
     */
    @Override
    public synchronized <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        // 如果已注册，就别注册了
        if(registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);

        // 添加到map
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length==0) throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        for(Class<?> i:interfaces){
            serviceMap.put(i.getCanonicalName(),service); // todo 这里是全类名
        }
        log.info("向接口:{}注册服务:{}",interfaces,serviceName);
    }

    @Override
    public Object getService(String interfaceName) {
        Object service = serviceMap.get(interfaceName);
        if(service==null) throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        return service;
    }
}
