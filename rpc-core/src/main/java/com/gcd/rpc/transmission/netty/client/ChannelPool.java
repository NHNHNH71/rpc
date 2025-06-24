package com.gcd.rpc.transmission.netty.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author nhnhnh7171
 * @Date 2025/6/24
 */
@Slf4j
public class ChannelPool {
    private final Map<String, Channel> pool = new ConcurrentHashMap<>();

    public Channel get(InetSocketAddress address, Supplier<Channel> supplier) {
        String addressString = address.toString();
        
        // 首先尝试获取已存在的channel
        Channel existingChannel = pool.get(addressString);
        if (existingChannel != null && existingChannel.isActive()) {
            log.info("存在活跃的Channel，直接返回");
            return existingChannel;
        }

        // 如果没有活跃的Channel，使用synchronized块确保只有一个线程创建新的Channel
        synchronized (this) {
            // 双重检查，防止其他线程已经创建了Channel
            existingChannel = pool.get(addressString);
            if (existingChannel != null && existingChannel.isActive()) {
                log.info("其他线程已创建Channel，直接返回");
                return existingChannel;
            }

            log.info("创建新的Channel：{}", addressString);
            Channel newChannel = supplier.get();
            
            // 添加关闭监听器，在Channel关闭时从池中移除
            newChannel.closeFuture().addListener(future -> {
                log.info("Channel已关闭，从池中移除：{}", addressString);
                pool.remove(addressString);
            });
            
            pool.put(addressString, newChannel);
            return newChannel;
        }
    }
}
