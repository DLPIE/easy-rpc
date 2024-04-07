package com.dl.rpc.server.rpcServers;

import com.dl.rpc.server.RpcTask;
import com.dl.rpc.server.ServiceProvider;
import com.dl.rpc.server.rpcServers.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// 监听指定端口，serviceImpl随时就绪
@Slf4j
public class SocketServer implements RpcServer {

    ServiceProvider serviceRegistry; // 注册表
    ThreadPoolExecutor threadPoolExecutor;
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAXIMUM_POOL_SIZE = 8;
    private static final int KEEP_ALIVE_TIME = 90;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    public SocketServer(ServiceProvider serviceRegistry){
        this.serviceRegistry=serviceRegistry;
        threadPoolExecutor=new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS,new LinkedBlockingQueue<>(BLOCKING_QUEUE_CAPACITY));
    }

    @Override
    public void listen(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port); // 监听的端口别漏!!
            log.info("服务端正在启动...");
            Socket socket;
            while((socket= serverSocket.accept())!=null){ // 监听到客户端连接
                log.info("客户端连接成功...IP为：" + socket.getInetAddress());
                // 提交给线程池处理，提高并发能力
                threadPoolExecutor.execute(new RpcTask(socket,serviceRegistry));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
