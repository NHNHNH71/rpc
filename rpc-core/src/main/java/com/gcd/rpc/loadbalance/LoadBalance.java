package com.gcd.rpc.loadbalance;

import com.gcd.rpc.dto.RpcReq;

import java.util.List;

/**
 * @author nhnhnh7171
 * @Date 2025/6/18
 */
public interface LoadBalance {
    public String select(List<String> list, RpcReq rpcReq);
}
