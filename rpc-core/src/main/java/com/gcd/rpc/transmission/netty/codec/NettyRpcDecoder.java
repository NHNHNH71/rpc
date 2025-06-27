package com.gcd.rpc.transmission.netty.codec;

import com.gcd.rpc.compress.Compress;
import com.gcd.rpc.compress.impl.GzipCompress;
import com.gcd.rpc.config.RpcConfig;
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
import com.gcd.rpc.serialize.Serializer;
import com.gcd.rpc.serialize.impl.KryoSerializer;
import com.gcd.rpc.spi.CustomLoader;
import com.gcd.rpc.util.ConfigUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author nhnhnh7171
 * @Date 2025/6/21
 */
@Slf4j
public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {
    public NettyRpcDecoder(){
        super(RpcConstant.REQ_MAX_LEN,5,4,-9,0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            log.debug("解码器收到空帧");
            return null;
        }
        log.info("进入了decode方法");
        try {
            return decodeFrame(frame);
        } finally {
            frame.release();
        }
    }

    private Object decodeFrame(ByteBuf byteBuf){
        if (byteBuf == null) {
            return null;
        }
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
        Object data=readData(byteBuf,msgLen-RpcConstant.REQ_HEAD_LEN,msgType,serializeType,compressType);
        log.info("decode完成，消息体长度：{}",msgLen-RpcConstant.REQ_HEAD_LEN);
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
        if (byteBuf == null) {
            throw new RpcException("ByteBuf为空，无法读取魔法数");
        }
        RpcConfig rpcConfig = ConfigUtils.getRpcConfig();
        byte[] magicBytes=new byte[rpcConfig.getRPC_MAGIC_CODE().length];

        byteBuf.readBytes(magicBytes);
        if(!Arrays.equals(magicBytes,rpcConfig.getRPC_MAGIC_CODE())){
            throw new RpcException("魔法数异常,收到的魔法数为:"+new String(magicBytes));
        }
    }
    private Object readData(ByteBuf byteBuf, int dataLen, MsgType msgType,SerializeType serializeType,CompressType compressType){
        if(msgType.isReq()){
            return readData(byteBuf,dataLen, RpcReq.class,serializeType,compressType);
        }
        return readData(byteBuf,dataLen, RpcResp.class,serializeType,compressType);
    }
    private <T> T readData(ByteBuf byteBuf,int dataLen,Class<T> clazz,SerializeType serializeType,CompressType compressType){
        if(dataLen<=0) return null;
        byte[] data=new byte[dataLen];
        byteBuf.readBytes(data);
        //获取序列化器和压缩工具的加载器
        CustomLoader<Serializer> serializerCustomLoader = CustomLoader.getLoader(Serializer.class);
        CustomLoader<Compress> compressCustomLoader = CustomLoader.getLoader(Compress.class);
        //根据配置加载序列化器和压缩工具
        Serializer serializer = serializerCustomLoader.get(serializeType.getDesc());
        Compress compress=compressCustomLoader.get(compressType.getDesc());
        data=compress.decompress(data);
        return serializer.deserialize(data,clazz);
    }
}
