package com.gcd.rpc.transmission;

import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;

import java.util.concurrent.Future;

public interface RpcClient {
    Future<RpcResp<?>> sendReq(RpcReq req);
}
