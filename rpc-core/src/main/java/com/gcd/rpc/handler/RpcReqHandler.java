package com.gcd.rpc.handler;

import com.gcd.rpc.annotation.Limit;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.exception.RpcException;
import com.gcd.rpc.provider.ServiceProvider;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.shaded.com.google.common.util.concurrent.RateLimiter;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author nhnhnh7171
 * @Date 2025/6/16
 */
@Data
@Slf4j
public class RpcReqHandler {
    private final ServiceProvider serviceProvider;
    private final static Map<String, RateLimiter> RATE_LIMITER_MAP=new ConcurrentHashMap<>();
    public RpcReqHandler(ServiceProvider sp){
        serviceProvider=sp;
    }
    //自动try catch并抛出异常，不用自己手动处理异常
    @SneakyThrows
    public Object invoke(RpcReq rpcReq) {
        try {
            String rpcServiceName = rpcReq.rpcServiceName();
            Object service = serviceProvider.getService(rpcServiceName);
            log.info("获取到服务：{}", service.getClass().getCanonicalName());
            Method method = service.getClass().getMethod(rpcReq.getMethodName(), rpcReq.getParamTypes());
            log.info("获取到方法，开始反射");
            Limit limit=method.getAnnotation(Limit.class);
            if(Objects.isNull(limit)){
                return method.invoke(service, rpcReq.getParams());
            }
            //  __表示占位符，表示不需要参数
            RateLimiter rateLimiter = RATE_LIMITER_MAP.computeIfAbsent(rpcServiceName,__ ->
                    RateLimiter.create(limit.requestsPerSecond()));

            if(!rateLimiter.tryAcquire(limit.timeout(),TimeUnit.MILLISECONDS)){
                //当方法被限流了的处理
                throw new RpcException("系统承受流量达到上线");
            }
            return method.invoke(service, rpcReq.getParams());
        } catch (InvocationTargetException e) {
            // 获取原始异常
            Throwable targetException = e.getTargetException();
            log.error("方法调用异常", targetException);
            throw targetException;
        }
    }
}
