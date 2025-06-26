package com.gcd.rpc.serialize.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.gcd.rpc.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author nhnhnh7171
 * @Date 2025/6/26
 */
public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        try (ByteArrayOutputStream oos=new ByteArrayOutputStream()){
            HessianOutput output=new HessianOutput(oos);
            output.writeObject(object);
            return oos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream inputStream=new ByteArrayInputStream(bytes)){
            HessianInput input=new HessianInput(inputStream);
            Object o=input.readObject();
            return clazz.cast(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
