package com.gcd.rpc.transmission.netty.RpcClient;

import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.dto.RpcMsg;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.enums.CompressType;
import com.gcd.rpc.enums.MsgType;
import com.gcd.rpc.enums.SerializeType;
import com.gcd.rpc.enums.VersionType;
import com.gcd.rpc.factory.SingletonFactory;
import com.gcd.rpc.proxy.RpcClientProxy;
import com.gcd.rpc.registry.ServiceDiscovery;
import com.gcd.rpc.registry.impl.ZKServiceDiscovery;
import com.gcd.rpc.transmission.RpcClient;
import com.gcd.rpc.transmission.netty.codec.NettyRpcDecoder;
import com.gcd.rpc.transmission.netty.codec.NettyRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nhnhnh7171
 * @Date 2025/6/20
 */
@Slf4j
public class NettyRpcClient implements RpcClient {
    private final ServiceDiscovery serviceDiscovery;
    private static final Bootstrap bootStrap;
    private static final int DEFAULT_CONNECT_TIMEOUT=5000;
    private static final AtomicInteger ID_GEN=new AtomicInteger(0);
    //对bootStrap进行初始化
    static {
        bootStrap=new Bootstrap();
        bootStrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,DEFAULT_CONNECT_TIMEOUT);
        bootStrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel)  {
                socketChannel.pipeline().addLast(new NettyRpcDecoder());
                socketChannel.pipeline().addLast(new NettyRpcEncoder());
                socketChannel.pipeline().addLast(new NettyClientHandler());
            }
        });
    }

    public NettyRpcClient() {
        this(SingletonFactory.getInstance(ZKServiceDiscovery.class));
    }

    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    @SneakyThrows
    public RpcResp<?> sendReq(RpcReq req) {
        InetSocketAddress address = serviceDiscovery.lookupService(req);
        log.info("开始发送请求...找到服务端地址：{}",address);
        ChannelFuture channelFuture = bootStrap.connect(address).sync();
        Channel channel = channelFuture.channel();
        if (!channel.isActive()) {
            log.error("Channel 未激活!");
            return null;
        }
        req.setReqId(String.valueOf(ID_GEN.incrementAndGet()));
        RpcMsg rpcMsg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .serializeType(SerializeType.KRYO)
                .data(req)
                .compressType(CompressType.GZIP)
                .reqId(Integer.valueOf(req.getReqId()))
                .msgType(MsgType.RPC_REQ)
                .build();
        
        log.info("准备发送数据: {}", rpcMsg);
        ChannelFuture writeFuture = channel.writeAndFlush(rpcMsg);
        writeFuture.addListener(future -> {
            if (future.isSuccess()) {
                log.info("消息发送成功");
            } else {
                log.error("消息发送失败", future.cause());
            }
        });
        
        channel.closeFuture().sync();
        log.info("请求完成");
        AttributeKey<RpcResp<?>> key=AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        return channel.attr(key).get();
    }
}
