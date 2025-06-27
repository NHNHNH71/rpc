package com.gcd.rpc.provider.impl;

import cn.hutool.core.util.StrUtil;
import com.gcd.rpc.config.RpcServiceConfig;
import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.factory.SingletonFactory;
import com.gcd.rpc.provider.ServiceProvider;
import com.gcd.rpc.registry.ServiceDiscovery;
import com.gcd.rpc.registry.ServiceRegistry;
import com.gcd.rpc.registry.impl.ZKServiceRegistry;
import com.gcd.rpc.util.ConfigUtils;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nhnhnh7171
 * @Date 2025/6/18
 */
public class ZKServiceProvider implements ServiceProvider {
    private final Map<String,Object> SERVICE_CACHE=new HashMap<>();
    private final ServiceRegistry serviceRegistry;

    public ZKServiceProvider() {
        this(SingletonFactory.getInstance(ZKServiceRegistry.class));
    }

    public ZKServiceProvider(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        config.rpcServiceNames()
                .forEach(rpcServiceName -> publishService(rpcServiceName,config.getService()));
    }

    @Override
    public Object getService(String rpcServiceName) {
        if(StrUtil.isBlank(rpcServiceName)) throw new IllegalArgumentException("rpcServiceName为空");
        if(!SERVICE_CACHE.containsKey(rpcServiceName)) throw new IllegalArgumentException("rpcService未注册，rpcServiceName:"+rpcServiceName);

        return SERVICE_CACHE.get(rpcServiceName);
    }
    @SneakyThrows
    private void publishService(String rpcServiceName,Object service){

        String host= InetAddress.getLocalHost().getHostAddress();
        int port= ConfigUtils.getRpcConfig().getSERVER_PORT();
        InetSocketAddress address = new InetSocketAddress(host, port);
        serviceRegistry.registerService(rpcServiceName,address);
        SERVICE_CACHE.put(rpcServiceName,service);
    }
}
