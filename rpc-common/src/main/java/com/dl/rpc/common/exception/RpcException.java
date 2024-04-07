package com.dl.rpc.common.exception;

import com.dl.rpc.common.exception.RpcError;

/**
 * 自定义运行时异常，提高程序健壮性，给程序员展现足够准确的错误信息，可以直接抛出。
 * 重写了三种构造方法，用于表示不同类型的异常。
 */
public class RpcException extends RuntimeException{
    public RpcException(RpcError error) {
        super(error.getMessage());
    }
    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
