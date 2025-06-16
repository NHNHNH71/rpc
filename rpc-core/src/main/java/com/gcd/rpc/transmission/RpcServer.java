package com.gcd.rpc.transmission;

import com.gcd.rpc.config.RpcServiceConfig;

public interface RpcServer {
    void start();
    void publishService(RpcServiceConfig config);
}
