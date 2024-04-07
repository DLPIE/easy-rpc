package com.dl.rpc.common.serialize;

public interface CommonSerializer {
    /**
     * 序列化
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     */
    Object deserialize(byte[] bytes,Class<?> clazz);

    int getCode();

    /**
     * 获取指定的序列化器
     */
    static CommonSerializer getByCode(int code){
        switch (code){
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
