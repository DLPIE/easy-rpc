package com.dl.rpc.client;

import com.dl.rpc.client.rpcClients.RpcClient;
import com.dl.rpc.client.rpcClients.SocketClient;
import com.dl.rpc.common.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 客户端代理：代理的是远程服务
public class RpcClientProxy {
    private final RpcClient rpcClient;

    public RpcClientProxy(RpcClient rpcClient) {
        this.rpcClient=rpcClient;
    }

    /**
     * 创建远程服务的客户端代理
     * @param clazz：远程服务，如userService
     * @return：本地代理
     * @param <T>
     */
    public <T> T getProxy(Class<T> clazz){ // 注意：如何声明泛型方法
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 对method增强
                // 封装RpcRequest
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setInterfaceName(method.getDeclaringClass().getCanonicalName()); // todo 反射
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setParamTypes(method.getParameterTypes());
                rpcRequest.setParamValues(args);
                // 3.将RpcRequest发到目标socket
                return rpcClient.sendRequest(rpcRequest);
            }
        });
    }
}
