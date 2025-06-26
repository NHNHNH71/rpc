package com.gcd.rpc.breaker;

import com.gcd.rpc.annotation.Breaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nhnhnh7171
 * @Date 2025/6/26
 */
public class CircuitBreakerManager {
    private static final Map<String,CircuitBreaker> BREAKER_MAP=new ConcurrentHashMap<>();
    public static CircuitBreaker getCircuitBreaker(String key, Breaker breaker){
        return BREAKER_MAP.computeIfAbsent(key,__->
                new CircuitBreaker(breaker.failThreshold(),breaker.successRateInHalfOpen(),breaker.windowTime()));
    }
}
