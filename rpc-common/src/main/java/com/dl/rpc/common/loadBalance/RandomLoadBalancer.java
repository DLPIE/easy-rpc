package com.dl.rpc.common.loadBalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{

    @Override
    public Instance getOneInstance(List<Instance> list) {
        int index=new Random().nextInt(list.size());
        return list.get(index);
    }
}
