package com.dl.rpc.common.loadBalance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.dl.rpc.common.util.NacosUtil;

import java.util.List;

public class RoundRobinLoadBalancer implements LoadBalancer{
    int index=0; // 计数器
    @Override
    public Instance getOneInstance(List<Instance> list) {
        if(index>=list.size()){ // index超标
            index=index%list.size();
        }
        return list.get(index++); // 顺序自增
    }
}
