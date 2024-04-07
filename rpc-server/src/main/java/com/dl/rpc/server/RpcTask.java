package com.dl.rpc.server;

import com.dl.rpc.common.RpcRequest;
import com.dl.rpc.common.RpcResponse;
import com.dl.rpc.server.provider.ServiceProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

// 封装线程池任务
public class RpcTask implements Runnable{
    Socket socket;
    // Object service;
    ServiceProvider serviceRegistry;

    public RpcTask(Socket socket, ServiceProvider serviceRegistry) {
        this.socket = socket;
        // this.service = service;
        this.serviceRegistry=serviceRegistry;
    }

    /**
     * 任务处理逻辑：从socket中取出RpcRequest，反射解析目标方法，让实现类执行，将方法返回值封装为RpcResponse返回
     */
    @Override
    public void run() {
        try {
            // 取出RpcRequest
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            RpcRequest rpcRequest=(RpcRequest)inputStream.readObject();
            // 从注册表寻找service
            String interfaceName = rpcRequest.getInterfaceName(); // todo 这里非全类名
            Object service = serviceRegistry.getService(interfaceName);
            // 执行目标方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object methodResult = method.invoke(service, rpcRequest.getParamValues());
            // 写回IO流
            outputStream.writeObject(RpcResponse.success(methodResult));
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
