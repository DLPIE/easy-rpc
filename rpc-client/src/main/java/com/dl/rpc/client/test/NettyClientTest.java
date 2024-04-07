package com.dl.rpc.client.test;

import com.dl.api.HelloService;
import com.dl.api.Message;
import com.dl.rpc.client.RpcClientProxy;
import com.dl.rpc.client.rpcClients.NettyClient;
import com.dl.rpc.common.serialize.JsonSerializer;

public class NettyClientTest {
    public static void main(String[] args) {
        // 创建远程服务(HelloService)的客户端代理
        NettyClient client = new NettyClient();
        client.setSerializer(new JsonSerializer());
        RpcClientProxy clientProxy = new RpcClientProxy(client);
        HelloService helloService = clientProxy.getProxy(HelloService.class); // 可以改进
        String res = helloService.hello(new Message(101, "嘿~我是客户端发来的消息"));// 像调本地方法一样调远程方法
        System.out.println(res);
    }
}
