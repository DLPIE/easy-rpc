package com.dl.rpc.common.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.dl.rpc.common.loadBalance.LoadBalancer;
import com.dl.rpc.common.loadBalance.RoundRobinLoadBalancer;
import com.dl.rpc.common.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery{
    private LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if(loadBalancer==null){
            this.loadBalancer=new RoundRobinLoadBalancer();// 默认顺序负载均衡
        }else{
            this.loadBalancer = loadBalancer;
        }
    }

    @Override
    public InetSocketAddress findService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            Instance instance = loadBalancer.getOneInstance(instances);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        } catch (NacosException e) {
            log.error("获得服务时发生错误");
        }
        return null;
    }
}
