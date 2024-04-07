package com.dl.rpc.client.rpcClients;

import com.dl.rpc.common.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest request);
}
