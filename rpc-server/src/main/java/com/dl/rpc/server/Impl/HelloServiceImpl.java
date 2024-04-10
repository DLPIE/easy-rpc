package com.dl.rpc.server.Impl;

import com.dl.api.HelloService;
import com.dl.api.Message;
import com.dl.rpc.common.annotation.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service // 标志这是一个服务，启动类扫描时会自动把它注册到Nacos和注册表
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(Message msg) {
        log.info("接收到数据："+msg.getData());
        return "方法返回值为:"+msg.getId();
    }
}
