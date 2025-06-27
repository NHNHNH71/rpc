package com.gcd.rpc.spi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.gcd.rpc.provider.ServiceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nhnhnh7171
 * @Date 2025/6/26
 */
@Slf4j
public class CustomLoader<T> {
    private static final String BASE_PATH="META-INF/gcd-rpc/";
    private final Class<T> type;
    private final Map<String,Holder<T>> objectCache=new ConcurrentHashMap<>();
    private final Map<String,Class<T>> clazzCache=new ConcurrentHashMap<>();
    private static final Map<Class<?>,CustomLoader<?>> LOADER_MAP=new ConcurrentHashMap<>();
    public CustomLoader(Class<T> type) {
        this.type = type;
    }
    public static <V>  CustomLoader<V> getLoader(Class<V> clazz){
        if(Objects.isNull(clazz)) throw new IllegalArgumentException("clazz不能为空");
        if(!clazz.isInterface()) throw new IllegalArgumentException("传入的clazz必须为接口");
        return (CustomLoader<V>) LOADER_MAP.computeIfAbsent(clazz,__->new CustomLoader<>(clazz));
    }
    public T get(String name){
        if(StrUtil.isBlank(name)) throw new IllegalArgumentException("name不能为空");
        Holder<T> holder=objectCache.computeIfAbsent(name, __->new Holder<>());
        T t=holder.getValue();
        if(t==null){
            synchronized (holder){
                t= holder.getValue();
                if(t==null){
                    holder.setValue(createObject(name));
                }
            }
        }
        return holder.getValue();
    }
    @SneakyThrows
    private T createObject(String name) {
        if(CollUtil.isEmpty(clazzCache)) loadDir();
        Class<T> clazz = clazzCache.get(name);
        return clazz.newInstance();
    }

    private void loadDir() throws IOException {
        String path=BASE_PATH+type.getName();
        ClassLoader classLoader=CustomLoader.class.getClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        if(CollUtil.isEmpty(resources)){
            throw new RuntimeException("文件不存在"+path);
        }
        while(resources.hasMoreElements()){
            URL url=resources.nextElement();
            loadResource(classLoader,url);
        }
    }

    @SneakyThrows
    private void loadResource(ClassLoader classLoader,URL url){
        try (BufferedReader reader=new BufferedReader(
                (new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)))){
            String line;
            while((line=reader.readLine())!=null){
                Pair<String, Class<T>> pair = handleLine(classLoader, line);
                if(pair==null) continue;
                clazzCache.put(pair.getKey(),pair.getValue());
            }
        }
    }
    @SneakyThrows
    private Pair<String,Class<T>> handleLine(ClassLoader classLoader,String line){
        line=line.trim();
        if(StrUtil.isBlank(line)) return null;
        String[] split=line.split("=");
        if(split.length!=2) throw new RuntimeException("行数据异常");
        Class<T> clazz=(Class<T>) classLoader.loadClass(split[1]);
        return new Pair<>(split[0],clazz);
    }
}
