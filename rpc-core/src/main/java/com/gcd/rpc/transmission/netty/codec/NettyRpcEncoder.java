package com.gcd.rpc.transmission.netty.codec;

import com.gcd.rpc.compress.impl.GzipCompress;
import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.dto.RpcMsg;
import com.gcd.rpc.factory.SingletonFactory;
import com.gcd.rpc.serialize.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nhnhnh7171
 * @Date 2025/6/21
 */
@Slf4j
public class NettyRpcEncoder extends MessageToByteEncoder<RpcMsg> {

    private static final AtomicInteger ID_GEN=new AtomicInteger(0);
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMsg msg, ByteBuf out){
        //为消息设置msgId
        msg.setReqId(ID_GEN.incrementAndGet());
        out.writeBytes(RpcConstant.RPC_MAGIC_CODE);
        out.writeByte(msg.getVersion().getCode());
        //消息总长度需要压缩后才能得到 先往右挪动四位预留位置
        out.writerIndex(out.writerIndex()+4);
        out.writeByte(msg.getMsgType().getCode());
        out.writeByte(msg.getSerializeType().getCode());
        out.writeByte(msg.getCompressType().getCode());
        out.writeInt(msg.getReqId());
        int msgLen=RpcConstant.REQ_HEAD_LEN;
        if(!msg.getMsgType().isHeartbeat()
         &&!Objects.isNull(msg.getData())){
            byte[] data=dataToBytes(msg);
            out.writeBytes(data);
            msgLen+=data.length;
        }
        int currIndex=out.writerIndex();
        out.writerIndex(currIndex-msgLen+RpcConstant.RPC_MAGIC_CODE.length+1);
        out.writeInt(msgLen);
        out.writerIndex(currIndex);

    }
    private byte[] dataToBytes(RpcMsg rpcMsg){
        // todo 获取序列化和数据压缩的类型再进行选择
        //先指定序列化和压缩器
        KryoSerializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        GzipCompress compress = SingletonFactory.getInstance(GzipCompress.class);
        byte[] data= serializer.serialize(rpcMsg.getData());
        return compress.compress(data);
    }
}
