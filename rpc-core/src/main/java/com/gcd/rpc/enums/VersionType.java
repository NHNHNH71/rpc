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
public enum VersionType {
    VERSION1((byte) 1,"版本1");
    private final byte code;
    private final String desc;
    public static VersionType getFromByte(byte code){
        return Arrays.stream(values())
                .filter(versionType -> versionType.code==code)
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException("异常：找不到code对应的版本,code:"+code));
    }
}
