package com.gcd.rpc.transmission.netty.RpcClient;

import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.dto.RpcResp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhnhnh7171
 * @Date 2025/6/20
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,String rpcResp) throws Exception {
        log.debug("client收到服务端的数据：{}",rpcResp);
        AttributeKey<String> key=AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        channelHandlerContext.attr(key).set(rpcResp);
        channelHandlerContext.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端发生了异常",cause);
        ctx.close();
    }
}
