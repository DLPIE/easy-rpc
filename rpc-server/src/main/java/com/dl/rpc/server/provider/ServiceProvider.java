package com.dl.rpc.server.provider;

public interface ServiceProvider {
    <T> void register(T service); // 注意泛型

    Object getService(String interfaceName);
}
