package com.dl.rpc.server.provider;

public interface ServiceProvider {
    /**
     * 注册到服务注册表
     * @param service
     * @param serviceName
     * @param <T>
     */
    <T> void register(T service, String serviceName);

    /**
     * 从注册表获取服务
     * @param serviceName
     * @return
     */
    Object getService(String serviceName);
}
