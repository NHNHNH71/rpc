package com.gcd.rpc.transmission;

import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;

public interface RpcClient {
    RpcResp<?> sendReq(RpcReq req);
}
