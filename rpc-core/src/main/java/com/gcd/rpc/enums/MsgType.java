package com.gcd.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

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
    public boolean isReq(){
        return this==HEARTBEAT_REQ||this==RPC_REQ;
    }
    public static MsgType getFromByte(byte code){
        return Arrays.stream(values())
                .filter(msgType -> msgType.code==code)
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException("异常：找不到code对应的消息类型,code:"+code));
    }
}
