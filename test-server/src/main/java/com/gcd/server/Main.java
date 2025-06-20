package com.gcd.server;

import com.gcd.api.UserService;
import com.gcd.rpc.config.RpcServiceConfig;
import com.gcd.rpc.proxy.RpcClientProxy;
import com.gcd.rpc.transmission.RpcServer;
import com.gcd.rpc.transmission.netty.RpcServcer.NettyRpcServer;
import com.gcd.rpc.transmission.socket.server.SocketRpcServer;
import com.gcd.server.Service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
//        RpcServiceConfig config=new RpcServiceConfig(new UserServiceImpl());
//        RpcServer server=new SocketRpcServer();
//        server.publishService(config);
//        server.start();
        NettyRpcServer server=new NettyRpcServer();
        server.start();

    }
}
