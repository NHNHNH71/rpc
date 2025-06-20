package com.gcd.server.nettyTest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhnhnh7171
 * @Date 2025/6/19
 */
@Slf4j
public class nettyServer {
    public static void main(String[] args) {
        ServerBootstrap bootstrap=new ServerBootstrap();
        NioEventLoopGroup bossEventLoopGroup=new NioEventLoopGroup();
        NioEventLoopGroup workerEventLoopGroup=new NioEventLoopGroup();
        bootstrap.group(bossEventLoopGroup,workerEventLoopGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                nioSocketChannel.pipeline().addLast(new StringDecoder());
                nioSocketChannel.pipeline().addLast(new StringEncoder());
                nioSocketChannel.pipeline().addLast(new MyServerHandler());
            }
        });
        bootstrap.bind(8081);
    }
    public static class MyServerHandler extends SimpleChannelInboundHandler<String>{

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
            log.info("服务器收到消息");
            String sendMsg="server msg 1";
            System.out.println("服务器发送消息："+sendMsg);
            channelHandlerContext.channel().writeAndFlush(sendMsg);
        }
    }
}
