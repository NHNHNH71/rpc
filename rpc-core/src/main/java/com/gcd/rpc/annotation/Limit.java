package com.gcd.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author nhnhnh7171
 * @Date 2025/6/25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {
    /**
     *每秒允许访问量
     */
    double requestsPerSecond();
    /**
     * 未拿到令牌的请求的最大等待时间
     */
    long timeout();
}
