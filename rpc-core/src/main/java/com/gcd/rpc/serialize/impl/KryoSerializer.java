package com.gcd.rpc.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author nhnhnh7171
 * @Date 2025/6/21
 */
@Slf4j
public class KryoSerializer implements Serializer {
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 注册需要序列化的类
        kryo.register(RpcReq.class);
        kryo.register(RpcResp.class);
        // 注册 Class[] 类型
        kryo.register(Class[].class);
        // 注册其他可能用到的类型
        kryo.register(Object[].class);
        kryo.register(String[].class);

        // 设置是否注册全限定类名，建议开启
        kryo.setRegistrationRequired(false);
        // 设置是否压缩序列化后的数据
        kryo.setReferences(true);

        return kryo;
    });

    @Override
    public byte[] serialize(Object object) {
        try (ByteArrayOutputStream oos = new ByteArrayOutputStream();
             Output output = new Output(oos)) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            kryo.writeObject(output, object);
            output.flush();
            return oos.toByteArray();
        } catch (Exception e) {
            log.error("kryo序列化失败", e);
            throw new RuntimeException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes);
             Input input = new Input(is)) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            return kryo.readObject(input, clazz);
        } catch (Exception e) {
            log.error("kryo反序列化失败", e);
            throw new RuntimeException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }
}
