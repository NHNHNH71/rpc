package com.gcd.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RpcRespStatus {
    SUCCESS(200,"success"),
    FAIL(500,"fail"),;
    private final int code;
    private final String msg;
    public static boolean isSuccessful(Integer code){
        return SUCCESS.getCode()==code;
    }
    public static boolean isFailed(Integer code){
        return !isSuccessful(code);
    }
}
