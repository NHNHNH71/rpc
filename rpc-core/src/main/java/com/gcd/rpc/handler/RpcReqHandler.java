package com.gcd.rpc.handler;

import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.provider.ServiceProvider;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author nhnhnh7171
 * @Date 2025/6/16
 */
@Data
@Slf4j
public class RpcReqHandler {
    private final ServiceProvider serviceProvider;
    public RpcReqHandler(ServiceProvider sp){
        serviceProvider=sp;
    }
    //自动try catch并抛出异常，不用自己手动处理异常
    @SneakyThrows
    public Object invoke(RpcReq rpcReq)  {
        String rpcServiceName=rpcReq.rpcServiceName();
        System.out.println(rpcServiceName);
        Object service= serviceProvider.getService(rpcServiceName);
        log.info("获取到服务：{}",service.getClass().getCanonicalName());
        Method method=service.getClass().getMethod(rpcReq.getMethodName(),rpcReq.getParamTypes());
        log.info("获取到方法，开始反射");
        return method.invoke(service,rpcReq.getParams());
    }
}
