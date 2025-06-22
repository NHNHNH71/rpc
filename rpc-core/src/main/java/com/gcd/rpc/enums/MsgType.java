package com.gcd.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author nhnhnh7171
 * @Date 2025/6/21
 */
@ToString
@Getter
@AllArgsConstructor
public enum MsgType {
    HEARTBEAT_REQ((byte) 1,"心跳请求消息"),

    HEARTBEAT_RESP((byte) 1,"心跳响应消息"),

    RPC_REQ((byte) 3,"rpc请求消息"),
    RPC_RESP((byte) 4,"rpc响应消息");
    private final byte code;
    private final String desc;
    public boolean isHeartbeat(){
        return this==HEARTBEAT_REQ||this==HEARTBEAT_RESP;
    }
}
