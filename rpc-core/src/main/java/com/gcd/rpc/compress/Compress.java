package com.gcd.rpc.compress;

/**
 * @author nhnhnh7171
 * @Date 2025/6/21
 */
public interface Compress {
    byte[] compress(byte[] data);
    byte[] decompress(byte[] data);
}
