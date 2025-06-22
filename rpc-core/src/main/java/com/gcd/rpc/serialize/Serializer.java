package com.gcd.rpc.serialize;

/**
 * @author nhnhnh7171
 * @Date 2025/6/21
 */
public interface Serializer {
    byte[] serialize(Object object);
    <T> T deserialize(byte[] bytes,Class<T> clazz);
}
