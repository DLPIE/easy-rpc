package com.dl.rpc.common.hook;

import com.dl.rpc.common.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShutdownHook {

    // 单例模式
    private static final ShutdownHook shutdownHook=new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    // 方法：在jvm关闭时，创建一个新线程，执行服务注销
    public void addClearAllHook() {
        log.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{ // 必须用lamda表达式
            NacosUtil.deRegisterService();
        }));
    }
}
