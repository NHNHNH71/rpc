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
public enum CompressType {
    GZIP((byte) 1,"gzip");
    private final byte code;
    private final String desc;
}
