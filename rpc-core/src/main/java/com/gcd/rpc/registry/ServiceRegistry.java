package com.gcd.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author nhnhnh7171
 * @Date 2025/6/18
 */
public interface ServiceRegistry {
    void registerService(String rpcServiceName, InetSocketAddress address);
    void clearAll();
}
