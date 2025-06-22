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
public enum CompressType {
    GZIP((byte) 1,"gzip");
    private final byte code;
    private final String desc;
    public static CompressType getFromByte(byte code){
        return Arrays.stream(values())
                .filter(compressType -> compressType.code==code)
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException("异常：找不到code对应的压缩类型,code:"+code));
    }
}
