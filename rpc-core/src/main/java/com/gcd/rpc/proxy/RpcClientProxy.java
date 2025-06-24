package com.gcd.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import com.gcd.rpc.config.RpcServiceConfig;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.enums.RpcRespStatus;
import com.gcd.rpc.exception.RpcException;
import com.gcd.rpc.transmission.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

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
    public Object invoke(Object proxy, Method method, Object[] args) {
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
        RpcResp<?> resp = rpcClient.sendReq(req);
        log.info("方法执行结束,结果：{}",resp);
        check(req,resp);
        return resp.getData();
    }
    private void check(RpcReq req,RpcResp<?> rpcResp){

        if(Objects.isNull(rpcResp)){
            throw new RpcException("响应内容为空");
        }
        log.debug("请求id:"+req.getReqId()+"响应id:"+rpcResp.getReqId());
        if(!Objects.equals(req.getReqId(),rpcResp.getReqId())){
            throw new RpcException("请求和响应的id不一致");
        }
        if(RpcRespStatus.isFailed(rpcResp.getCode())){
            throw new RpcException("响应码显示失败"+rpcResp.getMsg());
        }
    }
}
