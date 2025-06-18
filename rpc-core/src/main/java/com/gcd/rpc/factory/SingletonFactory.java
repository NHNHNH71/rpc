package com.gcd.rpc.factory;

import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author nhnhnh7171
 * @Date 2025/6/18
 */
public class SingletonFactory {
    private static final Map<Class<?>,Object> INSTANCE_CACHE=new HashMap<>();
    private SingletonFactory(){
    }
    @SneakyThrows
    public static <T> T getInstance(Class<T> clazz) {
        if(Objects.isNull(clazz)) throw new IllegalArgumentException("clazz不能为空");
        if(INSTANCE_CACHE.containsKey(clazz)){
            return clazz.cast(INSTANCE_CACHE.get(clazz));
        }
        synchronized (SingletonFactory.class){
            if(INSTANCE_CACHE.containsKey(clazz))
                return clazz.cast(INSTANCE_CACHE.get(clazz));
            T t=clazz.getConstructor().newInstance();
            INSTANCE_CACHE.put(clazz,t);
            return t;
        }
    }

}
