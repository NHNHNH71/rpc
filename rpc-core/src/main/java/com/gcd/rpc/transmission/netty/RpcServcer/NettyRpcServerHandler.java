package com.gcd.rpc.transmission.netty.RpcServcer;

import com.gcd.rpc.dto.RpcMsg;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.enums.CompressType;
import com.gcd.rpc.enums.MsgType;
import com.gcd.rpc.enums.SerializeType;
import com.gcd.rpc.enums.VersionType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhnhnh7171
 * @Date 2025/6/20
 */
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMsg rpcMsg) throws Exception {
        log.debug("接收到了客户端的请求:{}",rpcMsg);
        RpcReq rpcReq=(RpcReq) rpcMsg.getData();
        RpcResp<String> rpcResp = RpcResp.success(String.valueOf(rpcMsg.getReqId()),"这是相应数据");
        RpcMsg respMsg = RpcMsg.builder()
                .reqId(rpcMsg.getReqId())
                .version(VersionType.VERSION1)
                .msgType(MsgType.RPC_RESP)
                .data(rpcResp)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .build();
        channelHandlerContext.channel().writeAndFlush(respMsg)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端handler发生异常",cause);
        ctx.close();
    }
}
