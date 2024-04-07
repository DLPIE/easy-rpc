package com.dl.rpc.common.serialize;

import com.dl.rpc.common.RpcRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonSerializer implements CommonSerializer{

    private ObjectMapper objectMapper=new ObjectMapper();// Jackson库核心类，用于序列化和反序列化Java对象。

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj); // easy
        } catch (JsonProcessingException e) {
            log.error("序列化时有错误发生：{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if(obj instanceof RpcRequest){
                obj=handleRequest(obj); // 处理一下防止JSON反序列化出错
            }
            return obj;
        } catch (IOException e) {
            log.error("反序列化时有错误发生：{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类型，所以配合paramTypes转换
     * 需要重新判断处理
     * @param obj
     * @return
     */
    private Object handleRequest(Object obj) throws IOException{
        RpcRequest rpcRequest = (RpcRequest) obj;
        for(int i = 0; i < rpcRequest.getParamTypes().length; i++){
            // 规定的参数类型
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            // 实际类型不是规定的类型->转为规定的类型
            if(!clazz.isAssignableFrom(rpcRequest.getParamValues()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParamValues()[i]);
                rpcRequest.getParamValues()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        return 0;
    }
}
