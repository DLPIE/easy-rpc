package com.dl.rpc.common.coder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 数据包类型
 */
@Getter
@AllArgsConstructor
public enum PackageType {
    REQUEST_PACK(0), // 请求
    RESPONSE_PACK(1); // 响应
    private final int code;
}
