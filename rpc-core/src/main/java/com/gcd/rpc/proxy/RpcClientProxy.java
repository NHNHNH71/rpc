package com.gcd.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import com.gcd.rpc.annotation.Breaker;
import com.gcd.rpc.annotation.Retry;
import com.gcd.rpc.breaker.CircuitBreaker;
import com.gcd.rpc.breaker.CircuitBreakerManager;
import com.gcd.rpc.config.RpcServiceConfig;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.enums.MsgType;
import com.gcd.rpc.enums.RpcRespStatus;
import com.gcd.rpc.exception.RpcException;
import com.gcd.rpc.transmission.RpcClient;
import com.github.rholder.retry.*;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author nhnhnh7171
 * @Date 2025/6/17
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {
    private final RpcClient rpcClient;
    private final RpcServiceConfig config;

    public RpcClientProxy(RpcClient rpcClient) {
        this(rpcClient,new RpcServiceConfig());
    }

    public RpcClientProxy(RpcClient rpcClient, RpcServiceConfig config) {
        this.rpcClient = rpcClient;
        this.config = config;
    }
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        System.out.println("方法将要被执行");
        RpcReq req = RpcReq.builder()
                .reqId(IdUtil.fastSimpleUUID())
                .interfaceName(method.getDeclaringClass().getCanonicalName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .version(config.getVersion())
                .group(config.getGroup())
                .build();
        Breaker breaker=method.getAnnotation(Breaker.class);
        if(Objects.isNull(breaker)) return sendReqWithRetry(method, req);
        CircuitBreaker circuitBreaker= CircuitBreakerManager.getCircuitBreaker(
                req.rpcServiceName(),breaker
        );
        log.info("开始判断是否可以发送");
        if(!circuitBreaker.canSendReq()){
            log.info("不可以发送");
            throw new RpcException(req.rpcServiceName()+"服务发生了熔断，请求发送失败");
        }
        log.info("可以开始发送");
        try {
            Object o= sendReqWithRetry(method,req);
            circuitBreaker.success();
            return o;
        } catch (Exception e) {
            log.info("捕获到了异常，fail了");
            circuitBreaker.fail();
            throw new Exception(e);
        }
    }

    private Object sendReqWithRetry(Method method, RpcReq req) throws ExecutionException, RetryException, InterruptedException {
        Retry retry= method.getAnnotation(Retry.class);
        //当目标方法没有retry注解时 直接发送并处理结果
        if(Objects.isNull(retry)) return sendReq(req);
        //通过retry注解将重试规则注入到retryer中
        Retryer<Object> retryer = RetryerBuilder.newBuilder()
                //当发生相应异常时重试
                //获取到响应消息时对响应进行check ->有问题则抛出异常-> 异常被retryer捕获并处理->开始重试
                .retryIfExceptionOfType(retry.value())
                .withStopStrategy(StopStrategies.stopAfterAttempt(retry.maxAttempts()))
                .withWaitStrategy(WaitStrategies.fixedWait(retry.delay(), TimeUnit.MILLISECONDS))
                .build();
        return retryer.call(() -> sendReq(req));
    }


    private Object sendReq(RpcReq req) throws ExecutionException, InterruptedException {
        Future<RpcResp<?>> future = rpcClient.sendReq(req);
        RpcResp<?> rpcResp=future.get();
        log.info("方法执行结束,结果：{}",rpcResp);
        check(req,rpcResp);
        return rpcResp.getData();
    }

    private void check(RpcReq req,RpcResp<?> rpcResp){
        log.info("进入到check方法");
        if(Objects.isNull(rpcResp)){
            throw new RpcException("响应内容为空");
        }
        if(!Objects.equals(req.getReqId(),rpcResp.getReqId())){
            throw new RpcException("请求和响应的id不一致");
        }
        if(RpcRespStatus.isFailed(rpcResp.getCode())){
            throw new RpcException("响应码显示失败,异常信息："+rpcResp.getMsg());
        }
    }
}
