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

@ToString
@Getter
@AllArgsConstructor
public enum CompressType {
    CUSTOM((byte) 0,"custom"),
    GZIP((byte) 1,"gzip");
    private final byte code;
    private final String desc;
    public static CompressType getFromByte(byte code){
        return Arrays.stream(values())
                .filter(compressType -> compressType.code==code)
                .findFirst()
                .orElse(CUSTOM);
    }
    public static CompressType getFromDesc(String desc){
        return Arrays.stream(values())
                .filter(compressType -> Objects.equals(compressType.desc, desc))
                .findFirst()
                .orElse(CUSTOM);
    }
}
