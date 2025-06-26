package com.gcd.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author nhnhnh7171
 * @Date 2025/6/26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Breaker {

    int failThreshold() default 5;

    double successRateInHalfOpen() default 0.6;

    long windowTime() default 10000;

}
