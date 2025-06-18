package com.gcd.rpc.provider.impl;

import cn.hutool.core.collection.CollUtil;
import com.gcd.rpc.config.RpcServiceConfig;
import com.gcd.rpc.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nhnhnh7171
 * @Date 2025/6/16
 */
@Slf4j
public class SimpleServiceProvider implements ServiceProvider {
    private final Map<String,Object> SERVICE_CACHE=new HashMap<>();
    @Override
    public void publishService(RpcServiceConfig config) {
        List<String> rpcServiceNames=config.rpcServiceNames();
        if(CollUtil.isEmpty(rpcServiceNames)){
            throw new RuntimeException("该服务未实现接口");
        }
        //log.info("成功发布服务:{}"+rpcServiceNames);
        rpcServiceNames.forEach(rpcServiceName-> {
            SERVICE_CACHE.put(rpcServiceName,config.getService());
            log.info("服务名：{}",rpcServiceName);
        });
    }

    @Override
    public Object getService(String rpcServiceName) {
        if(!SERVICE_CACHE.containsKey(rpcServiceName)){
            throw new IllegalArgumentException("找不到对应服务"+rpcServiceName);
        }
        return SERVICE_CACHE.get(rpcServiceName);
    }
}
