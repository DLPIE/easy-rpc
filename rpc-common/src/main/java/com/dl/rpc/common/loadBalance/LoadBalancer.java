package com.dl.rpc.common.loadBalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public interface LoadBalancer {
    Instance getOneInstance(List<Instance> list);
}
