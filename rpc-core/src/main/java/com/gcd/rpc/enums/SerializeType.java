package com.gcd.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author nhnhnh7171
 * @Date 2025/6/21
 */
@Getter
@ToString
@AllArgsConstructor
public enum SerializeType {
    CUSTOM((byte) 0,"custom"),
    KRYO((byte) 1,"kryo"),
    HESSIAN((byte) 2,"hessian"),
    PROTOSTUFF((byte) 3,"protostuff");
    private final byte code;
    private final String desc;
    public static SerializeType getFromByte(byte code){
        return Arrays.stream(values())
                .filter(serializeType -> serializeType.code==code)
                .findFirst()
                .orElse(CUSTOM);
    }
    public static SerializeType getFromDesc(String desc){
        return Arrays.stream(values())
                .filter(serializeType -> Objects.equals(serializeType.desc, desc))
                .findFirst()
                .orElse(CUSTOM);
    }
}
