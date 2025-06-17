package com.gcd.client.utils;

import com.gcd.rpc.proxy.RpcClientProxy;
import com.gcd.rpc.transmission.RpcClient;
import com.gcd.rpc.transmission.socket.client.SocketRpcClient;

/**
 * @author nhnhnh7171
 * @Date 2025/6/17
 */
public class ProxyUtils {
    private static final RpcClient rpcClient=new SocketRpcClient("localhost",8888);
    private static final RpcClientProxy proxy=new RpcClientProxy(rpcClient);
    //根据给定类返回相应类的代理,这里指的是userService这个被远程调用的类
    public static <T> T getProxy(Class<T> clazz){
        return proxy.getProxy(clazz);
    }
}
