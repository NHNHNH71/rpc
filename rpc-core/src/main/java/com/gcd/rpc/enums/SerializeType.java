package com.gcd.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author nhnhnh7171
 * @Date 2025/6/21
 */
@Getter
@ToString
@AllArgsConstructor
public enum SerializeType {
    KRYO((byte) 1,"kryo");
    private final byte code;
    private final String desc;
}
