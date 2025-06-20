package com.gcd.rpc.transmission.netty.RpcServcer;

import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhnhnh7171
 * @Date 2025/6/20
 */
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String rpcReq) throws Exception {
        log.debug("接收到了客户端的请求:{}",rpcReq);
//        RpcResp<String> rpcResp = RpcResp.success(rpcReq, "这是虚假的数据");
        String rpcResp="hello client";
        channelHandlerContext.channel().writeAndFlush(rpcResp)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端handler发生异常",cause);
        ctx.close();
    }
}
