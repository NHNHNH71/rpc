package com.gcd.rpc.transmission.netty.RpcServcer;

import com.gcd.rpc.config.RpcServiceConfig;
import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.factory.SingletonFactory;
import com.gcd.rpc.provider.ServiceProvider;
import com.gcd.rpc.provider.impl.ZKServiceProvider;
import com.gcd.rpc.transmission.RpcServer;
import com.gcd.rpc.transmission.netty.codec.NettyRpcDecoder;
import com.gcd.rpc.transmission.netty.codec.NettyRpcEncoder;
import com.gcd.rpc.util.ShutdownHookUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhnhnh7171
 * @Date 2025/6/20
 */
@Slf4j
public class NettyRpcServer implements RpcServer {
    private final ServerBootstrap serverBootstrap=new ServerBootstrap();
    private final ServiceProvider serviceProvider;
    private final int port;

    public NettyRpcServer() {
        this(RpcConstant.SERVER_PORT);
    }

    public NettyRpcServer(int port) {
        this(port,SingletonFactory.getInstance(ZKServiceProvider.class));
    }

    public NettyRpcServer(int port, ServiceProvider serviceProvider) {
        this.port = port;
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try {
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel){
                            channel.pipeline().addLast(new NettyRpcDecoder());
                            channel.pipeline().addLast(new NettyRpcEncoder());
                            channel.pipeline().addLast(new NettyRpcServerHandler(serviceProvider));
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            ShutdownHookUtils.addShutdownTask();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("服务端启动发生异常",e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }
}
