package com.gcd.rpc.spi;

/**
 * @author nhnhnh7171
 * @Date 2025/6/26
 */
public class Holder <T>{
    private volatile T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
