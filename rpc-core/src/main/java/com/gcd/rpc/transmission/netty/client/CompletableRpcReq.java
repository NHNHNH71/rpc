package com.gcd.rpc.transmission.netty.client;

import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.exception.RpcException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nhnhnh7171
 * @Date 2025/6/24
 */
public class CompletableRpcReq {
    private static final Map<String, CompletableFuture<RpcResp<?>>> FUTURE_MAP = new ConcurrentHashMap<>();

    public static void put(String reqId, CompletableFuture<RpcResp<?>> future) {
        FUTURE_MAP.put(reqId, future);
    }

    public static void complete(RpcResp<?> rpcResp) {
        CompletableFuture<RpcResp<?>> future = FUTURE_MAP.remove(rpcResp.getReqId());
        if (future != null) {
            future.complete(rpcResp);
        }
    }

    public static void completeAllExceptionally(Throwable cause) {
        FUTURE_MAP.forEach((reqId, future) -> {
            future.completeExceptionally(cause);
            FUTURE_MAP.remove(reqId);
        });
    }
}
