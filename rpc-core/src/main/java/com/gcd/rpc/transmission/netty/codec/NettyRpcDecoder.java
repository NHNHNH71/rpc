package com.gcd.rpc.transmission.netty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

/**
 * @author nhnhnh7171
 * @Date 2025/6/21
 */
public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {
    public NettyRpcDecoder(){
        this(0,0,0);
    }
    public NettyRpcDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }
}
