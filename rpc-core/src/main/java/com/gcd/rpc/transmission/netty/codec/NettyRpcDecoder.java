package com.gcd.rpc.transmission.netty.codec;

import com.gcd.rpc.compress.impl.GzipCompress;
import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.dto.RpcMsg;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.enums.CompressType;
import com.gcd.rpc.enums.MsgType;
import com.gcd.rpc.enums.SerializeType;
import com.gcd.rpc.enums.VersionType;
import com.gcd.rpc.exception.RpcException;
import com.gcd.rpc.factory.SingletonFactory;
import com.gcd.rpc.serialize.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author nhnhnh7171
 * @Date 2025/6/21
 */
public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {
    public NettyRpcDecoder(){
        super(RpcConstant.REQ_MAX_LEN,5,4,-9,0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame=(ByteBuf) super.decode(ctx, in);
        return decodeFrame(frame);
    }

    private Object decodeFrame(ByteBuf byteBuf){
        readAndCheckMagicCode(byteBuf);

        byte versionCode=byteBuf.readByte();
        VersionType versionType = VersionType.getFromByte(versionCode);

        int msgLen=byteBuf.readInt();

        byte msgTypeCode=byteBuf.readByte();
        MsgType msgType = MsgType.getFromByte(msgTypeCode);

        byte serializeTypeCode=byteBuf.readByte();
        SerializeType serializeType = SerializeType.getFromByte(serializeTypeCode);

        byte compressTypeCode=byteBuf.readByte();
        CompressType compressType = CompressType.getFromByte(compressTypeCode);

        int reqId=byteBuf.readInt();
        Object data=readData(byteBuf,msgLen-RpcConstant.REQ_HEAD_LEN,msgType);
        return RpcMsg.builder()
                .reqId(reqId)
                .compressType(compressType)
                .data(data)
                .msgType(msgType)
                .serializeType(serializeType)
                .version(versionType)
                .build();
    }
    private void readAndCheckMagicCode(ByteBuf byteBuf){
        byte[] magicBytes=new byte[RpcConstant.RPC_MAGIC_CODE.length];
        byteBuf.readBytes(magicBytes);
        if(!Arrays.equals(magicBytes,RpcConstant.RPC_MAGIC_CODE)){
            throw new RpcException("魔法数异常,收到的魔法数为:"+new String(magicBytes));
        }
    }
    private Object readData(ByteBuf byteBuf, int dataLen, MsgType msgType){
        if(msgType.isReq()){
            return readData(byteBuf,dataLen, RpcReq.class);
        }
        return readData(byteBuf,dataLen, RpcResp.class);
    }
    private <T> T readData(ByteBuf byteBuf,int dataLen,Class<T> clazz){
        if(dataLen<=0) return null;
        byte[] data=new byte[dataLen];
        byteBuf.readBytes(dataLen);
        GzipCompress compress = SingletonFactory.getInstance(GzipCompress.class);
        data=compress.decompress(data);
        KryoSerializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        return serializer.deserialize(data,clazz);
    }
}
