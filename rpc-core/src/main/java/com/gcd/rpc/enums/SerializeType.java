package com.gcd.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * @author nhnhnh7171
 * @Date 2025/6/21
 */
@Getter
@ToString
@AllArgsConstructor
public enum SerializeType {
    KRYO((byte) 1,"kryo"),
    HESSIAN((byte) 2,"hessian"),
    PROTOSTUFF((byte) 3,"protostuff");
    private final byte code;
    private final String desc;
    public static SerializeType getFromByte(byte code){
        return Arrays.stream(values())
                .filter(serializeType -> serializeType.code==code)
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException("异常：找不到code对应的序列化器类型,code:"+code));
    }
}
