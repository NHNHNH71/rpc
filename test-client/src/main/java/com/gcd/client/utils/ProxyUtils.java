package com.gcd.client.utils;

import com.gcd.rpc.proxy.RpcClientProxy;
import com.gcd.rpc.transmission.RpcClient;
import com.gcd.rpc.transmission.netty.client.NettyRpcClient;

/**
 * @author nhnhnh7171
 * @Date 2025/6/17
 */
public class ProxyUtils {
    private static final RpcClient rpcClient=new NettyRpcClient();
    private static final RpcClientProxy proxy=new RpcClientProxy(rpcClient);
    //根据给定类返回相应类的代理,这里指的是userService这个被远程调用的类
    public static <T> T getProxy(Class<T> clazz){
        return proxy.getProxy(clazz);
    }
}
