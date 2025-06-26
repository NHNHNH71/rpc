package com.gcd.rpc.serialize.impl;

import com.gcd.rpc.serialize.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @author nhnhnh7171
 * @Date 2025/6/26
 */
public class ProtostuffSerializer implements Serializer {
    //分配一个缓冲区 用于存放序列化的数据
    private static final LinkedBuffer BUFFER=LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    @Override
    public byte[] serialize(Object object) {
        Class<?> clazz=object.getClass();
        Schema schema= RuntimeSchema.getSchema(clazz);
        try {
            return ProtobufIOUtil.toByteArray(object,schema,BUFFER);
        } finally {
            BUFFER.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema=RuntimeSchema.getSchema(clazz);
        T t=schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes,t,schema);
        return t;
    }
}
