package com.dl.rpc;

import com.dl.api.HelloService;
import com.dl.api.Message;
import com.dl.rpc.client.RpcClientProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcClientApplication.class, args);
    }

}
