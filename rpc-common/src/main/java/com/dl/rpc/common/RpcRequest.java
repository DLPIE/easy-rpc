package com.dl.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    private String interfaceName;
    private String methodName;
    private Class<?>[] paramTypes; // 泛型：Class<T>[]表示具体的类型、Class<?>[]表示任意类型（通配符）
    private Object[] paramValues;// Object类型太模糊，反序列化会失败

}
