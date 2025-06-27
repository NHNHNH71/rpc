package com.gcd.api;

import com.gcd.rpc.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author nhnhnh7171
 * @Date 2025/6/27
 */
@Slf4j
public class MySerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        try (ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream=new ObjectOutputStream(byteArrayOutputStream)){
            log.info("使用了自定义序列化器-------------------------------------");
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream)){
            log.info("使用了自定义序列化器-------------------------------------");
            return clazz.cast(objectInputStream.readObject());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
