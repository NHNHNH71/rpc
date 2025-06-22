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
public enum VersionType {
    VERSION1((byte) 1,"版本1");
    private final byte code;
    private final String desc;
}
