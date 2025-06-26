package com.gcd.rpc.loadbalance.impl;

import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.loadbalance.LoadBalance;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author nhnhnh7171
 * @Date 2025/6/26
 */
public class ConsistentHashLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> list, RpcReq rpcReq) {
        String key=rpcReq.rpcServiceName();
        long hashCode= Hashing.murmur3_128().hashString(key, StandardCharsets.UTF_8).asLong();
        int index = Hashing.consistentHash(hashCode, list.size());
        return list.get(index);
    }
}
