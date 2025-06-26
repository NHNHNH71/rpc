package com.gcd.rpc.loadbalance.impl;

import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.loadbalance.LoadBalance;

import java.util.List;

/**
 * @author nhnhnh7171
 * @Date 2025/6/26
 */
public class RoundLoadBalance implements LoadBalance {
    private int last=-1;
    @Override
    public String select(List<String> list, RpcReq rpcReq) {
        last++;
        last=last%list.size();
        return list.get(last);
    }
}
