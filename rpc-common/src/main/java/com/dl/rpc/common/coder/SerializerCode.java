package com.dl.rpc.common.coder;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 定义了枚举类来存储序列化器的编号。
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {
    JSON(1);

    private final int code;
}
