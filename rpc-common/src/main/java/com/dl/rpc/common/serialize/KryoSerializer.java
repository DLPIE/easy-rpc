package com.dl.rpc.common.serialize;

public class KryoSerializer implements CommonSerializer{
    @Override
    public byte[] serialize(Object obj) {
        return new byte[0];
    }

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        return null;
    }
}
