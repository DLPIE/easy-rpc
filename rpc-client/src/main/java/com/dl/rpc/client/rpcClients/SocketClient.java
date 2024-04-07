package com.dl.rpc.client.rpcClients;

import com.dl.rpc.client.rpcClients.RpcClient;
import com.dl.rpc.common.ResponseCode;
import com.dl.rpc.common.RpcRequest;
import com.dl.rpc.common.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class SocketClient implements RpcClient {
    private String ip;
    private int port;

    public SocketClient() {
    }

    public SocketClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Object sendRequest(RpcRequest rpcRequest){
        // 尝试与服务端创建socket连接，使用IO流发送数据
        try(Socket socket = new Socket(ip, port)){
            // socket流->对象流
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            // 写入缓冲区
            outputStream.writeObject(rpcRequest);
            // 发到服务端
            outputStream.flush();
            // 读取返回值（阻塞IO）
            RpcResponse rpcResponse = (RpcResponse) inputStream.readObject();
            // 异常处理
            if(rpcResponse==null){
                log.error("rpcResponse为空，接口名:",rpcRequest.getInterfaceName());
                throw new RuntimeException("rpcResponse为空，接口名:"+rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getStatusCode()==null || rpcResponse.getStatusCode()!= ResponseCode.SUCCESS.getCode()){
                log.error("状态码为空/状态码不是SUCCESS，接口名:",rpcRequest.getInterfaceName());
                throw new RuntimeException("状态码为空/状态码不是SUCCESS，接口名:"+rpcRequest.getInterfaceName());
            }
            // 获取方法的返回值
            return rpcResponse.getData();
        }catch(Exception e){
            log.error("rpc发生异常:",e);
            throw new RuntimeException("rpc发生异常:"+e);
        }
    }
}
