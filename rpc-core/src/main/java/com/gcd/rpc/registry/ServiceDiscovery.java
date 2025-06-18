package com.gcd.rpc.registry;

import com.gcd.rpc.dto.RpcReq;

import java.net.InetSocketAddress;

/**
 * @author nhnhnh7171
 * @Date 2025/6/18
 */
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcReq req);
}
