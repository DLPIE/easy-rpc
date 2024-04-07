package com.dl.rpc.server;

import com.dl.api.HelloService;
import com.dl.api.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(Message msg) {
        log.info("接收到数据："+msg.getData());
        return "方法返回值为:"+msg.getId();
    }
}
