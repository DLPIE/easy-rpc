package com.dl.rpc.client.rpcClients;

import com.dl.rpc.common.RpcRequest;
import com.dl.rpc.common.serialize.CommonSerializer;

public interface RpcClient {
    Object sendRequest(RpcRequest request);

    void setSerializer(CommonSerializer serializer);
}
