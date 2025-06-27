package com.gcd.rpc.transmission.netty.server;

import com.gcd.rpc.config.RpcConfig;
import com.gcd.rpc.dto.RpcMsg;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.enums.CompressType;
import com.gcd.rpc.enums.MsgType;
import com.gcd.rpc.enums.SerializeType;
import com.gcd.rpc.enums.VersionType;
import com.gcd.rpc.handler.RpcReqHandler;
import com.gcd.rpc.provider.ServiceProvider;
import com.gcd.rpc.util.ConfigUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhnhnh7171
 * @Date 2025/6/20
 */
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMsg> {
    private final RpcReqHandler rpcReqHandler;


    public NettyRpcServerHandler(ServiceProvider serviceProvider) {
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMsg rpcMsg)   {
        log.debug("接收到了客户端的请求:{}",rpcMsg);
        MsgType msgType;
        Object data;
        if(rpcMsg.getMsgType().isHeartbeat()){
            msgType=MsgType.HEARTBEAT_RESP;
            data=null;
        }else{
            msgType=MsgType.RPC_RESP;
            RpcReq req=(RpcReq) rpcMsg.getData();
            data = handleRpcReq(req);
        }
        RpcConfig rpcConfig= ConfigUtils.getRpcConfig();
        RpcMsg respMsg = RpcMsg.builder()
                .reqId(rpcMsg.getReqId())
                .version(VersionType.VERSION1)
                .msgType(msgType)
                .data(data)
                .serializeType(SerializeType.getFromDesc(rpcConfig.getSerializer()))
                .compressType(CompressType.getFromDesc(rpcConfig.getCompress()))
                .build();
        log.info("服务端返回信息:{}",respMsg);
        channelHandlerContext.channel().writeAndFlush(respMsg)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)   {
        log.error("服务端handler发生异常",cause);
        ctx.close();
    }
    private RpcResp<?> handleRpcReq(RpcReq rpcReq){
        try {
            Object data=rpcReqHandler.invoke(rpcReq);
            return RpcResp.success(rpcReq.getReqId(),data);
        }catch (Exception e){
            log.error("服务端调用方法异常，{}",e.getMessage());
            return RpcResp.fail(rpcReq.getReqId(),e.getMessage());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        boolean needToBeClosed=evt instanceof IdleStateEvent &&((IdleStateEvent)evt).state()==IdleState.READER_IDLE;
        if(needToBeClosed){
            log.info("服务端15秒内未收到客户端的消息，关闭channel,addr:{}",ctx.channel().remoteAddress());
            ctx.channel().close();
        }else{
            super.userEventTriggered(ctx, evt);
        }

    }
}
