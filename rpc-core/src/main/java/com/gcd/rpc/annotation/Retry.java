package com.gcd.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author nhnhnh7171
 * @Date 2025/6/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retry {
    //注解对于什么类型生效
    Class<? extends Throwable> value() default Exception.class;
    //最大重试次数
    int maxAttempts() default 2;
    //重试间隔
    int delay() default 0;
}
