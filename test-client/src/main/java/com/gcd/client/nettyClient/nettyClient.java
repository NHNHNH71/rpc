package com.gcd.client.nettyClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * @author nhnhnh7171
 * @Date 2025/6/19
 */
@Slf4j
public class nettyClient {
    public static void main(String[] args) {
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new StringDecoder());
                socketChannel.pipeline().addLast(new StringEncoder());
                socketChannel.pipeline().addLast(new MyClientHandler());
            }
        });
        Channel channel=bootstrap.connect("localhost",8081)
                .channel();
        Scanner sc=new Scanner(System.in);
        while (true) {
            String s=sc.nextLine();
            if(s.equals("a")) channel.writeAndFlush("hello");
        }
    }
    public static class MyClientHandler extends SimpleChannelInboundHandler<String>{

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
            log.info("收到服务器返回的消息,{}",s);;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            String msg="client msg 1";
            log.info("client 发送消息,{}",msg);
            ctx.channel().writeAndFlush(msg);
        }
    }
}
