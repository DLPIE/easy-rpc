package com.dl.rpc.common.registry;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress findService(String serviceName);
}
