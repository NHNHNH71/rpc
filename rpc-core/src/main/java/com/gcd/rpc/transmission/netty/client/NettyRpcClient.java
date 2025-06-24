package com.gcd.rpc.transmission.netty.client;

import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.dto.RpcMsg;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.enums.CompressType;
import com.gcd.rpc.enums.MsgType;
import com.gcd.rpc.enums.SerializeType;
import com.gcd.rpc.enums.VersionType;
import com.gcd.rpc.factory.SingletonFactory;
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
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author nhnhnh7171
 * @Date 2025/6/20
 */
@Slf4j
public class NettyRpcClient implements RpcClient {
    private final ServiceDiscovery serviceDiscovery;
    private static final Bootstrap bootStrap;
    private final ChannelPool channelPool;
    private static final int DEFAULT_CONNECT_TIMEOUT=5000;
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
                socketChannel.pipeline().addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
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
        this.channelPool=SingletonFactory.getInstance(ChannelPool.class);
    }

    @Override
    @SneakyThrows
    public RpcResp<?> sendReq(RpcReq req) {
        InetSocketAddress address = serviceDiscovery.lookupService(req);
        log.info("开始发送请求...找到服务端地址：{}",address);
        CompletableFuture<RpcResp<?>> completableFuture=new CompletableFuture<>();
        CompletableRpcReq.put(req.getReqId(),completableFuture);
        Channel channel = channelPool.get(address,()->connect(address));
        RpcMsg rpcMsg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .serializeType(SerializeType.KRYO)
                .data(req)
                .compressType(CompressType.GZIP)
                //.reqId(Integer.valueOf(req.getReqId()))
                .msgType(MsgType.RPC_REQ)
                .build();
        
        log.info("准备发送数据: {}", rpcMsg);
        channel.writeAndFlush(rpcMsg).addListener((ChannelFutureListener) listener->{
            if(!listener.isSuccess()){
                listener.channel().close();
                log.info("发送数据失败了");
                completableFuture.completeExceptionally(listener.cause());
            }else log.info("发送数据成功");
        });
        return completableFuture.get();
    }
    private Channel connect(InetSocketAddress addr){
        try {
            return bootStrap.connect(addr).sync().channel();
        } catch (InterruptedException e) {
            log.error("连接到服务器失败",e);
            throw new RuntimeException();
        }
    }
}
