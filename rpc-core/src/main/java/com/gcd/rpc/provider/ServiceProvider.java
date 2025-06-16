package com.gcd.rpc.provider;

import com.gcd.rpc.config.RpcServiceConfig;

/**
 * @author nhnhnh7171
 * @Date 2025/6/16
 */
public interface ServiceProvider {
    void publishService(RpcServiceConfig config);
    Object getService(String rpcServiceName);
}
