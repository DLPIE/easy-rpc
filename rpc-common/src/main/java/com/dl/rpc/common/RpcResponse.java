package com.dl.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> implements Serializable { // 注意泛型T
    private Integer statusCode; // 状态码
    private String message; // 错误信息
    private T data; // 方法返回值

    // 快速构建成功的RpcResponse
    public static <T> RpcResponse<T> success(T data){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    // 快速构建失败的RpcResponse
    public static <T> RpcResponse<T> fail(ResponseCode responseCode){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(responseCode.getCode());
        response.setMessage(responseCode.getMessage());
        return response;
    }

}
