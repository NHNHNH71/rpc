package com.gcd.rpc.transmission.netty.client;

import com.gcd.rpc.config.RpcConfig;
import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.dto.RpcMsg;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.enums.CompressType;
import com.gcd.rpc.enums.MsgType;
import com.gcd.rpc.enums.SerializeType;
import com.gcd.rpc.enums.VersionType;
import com.gcd.rpc.util.ConfigUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhnhnh7171
 * @Date 2025/6/20
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,RpcMsg rpcMsg) throws Exception {
        log.debug("client收到服务端的数据：{}",rpcMsg);
        if(rpcMsg.getMsgType()==MsgType.HEARTBEAT_RESP){
            log.info("收到心跳响应,reqId：{}",rpcMsg.getReqId());
            return ;
        }
        RpcResp<?> rpcResp=(RpcResp<?>) rpcMsg.getData();
        CompletableRpcReq.complete(rpcResp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端发生了异常",cause);
        CompletableRpcReq.completeAllExceptionally(cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接已关闭：{}", ctx.channel().remoteAddress());
        CompletableRpcReq.completeAllExceptionally(new RuntimeException("连接已关闭"));
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //判断是否为5秒内没有写事件
        boolean needHeartReq=evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state()== IdleState.WRITER_IDLE;
        if(!needHeartReq){
            super.userEventTriggered(ctx,evt);
            return ;
        }
        RpcConfig rpcConfig= ConfigUtils.getRpcConfig();
        RpcMsg rpcMsg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .serializeType(SerializeType.getFromDesc(rpcConfig.getSerializer()))
                .compressType(CompressType.getFromDesc(rpcConfig.getCompress()))
                .msgType(MsgType.HEARTBEAT_REQ)
                .build();
        log.info("客户端发送了一个心跳,{}",rpcMsg);
        ctx.writeAndFlush(rpcMsg)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
}
