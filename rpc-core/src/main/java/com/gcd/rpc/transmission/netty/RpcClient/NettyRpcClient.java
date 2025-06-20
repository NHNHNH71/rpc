package com.gcd.rpc.transmission.netty.RpcClient;

import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.factory.SingletonFactory;
import com.gcd.rpc.proxy.RpcClientProxy;
import com.gcd.rpc.registry.ServiceDiscovery;
import com.gcd.rpc.registry.impl.ZKServiceDiscovery;
import com.gcd.rpc.transmission.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author nhnhnh7171
 * @Date 2025/6/20
 */
@Slf4j
public class NettyRpcClient implements RpcClient {
    private final ServiceDiscovery serviceDiscovery;
    private static final Bootstrap bootStrap;
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
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new StringDecoder());
                socketChannel.pipeline().addLast(new StringEncoder());
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
        //InetSocketAddress address=serviceDiscovery.lookupService(req);
        ChannelFuture channelFuture=bootStrap.connect(RpcConstant.ZK_IP,RpcConstant.SERVER_PORT)
                .sync();
        Channel channel=channelFuture.channel();

        channel.writeAndFlush(req.getInterfaceName()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        channel.closeFuture().sync();
        //通过channel的一个map获取到服务端的响应内容
        //在自定义的handler里面将响应内容放到了这个map里面
        AttributeKey<String> key=AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        log.debug("获取响应数据:{}",channel.attr(key).get());
        return null;
    }
    @SneakyThrows
    public void connect(){
        ChannelFuture channelFuture = bootStrap.connect("localhost", 8080).sync();

    }
}
