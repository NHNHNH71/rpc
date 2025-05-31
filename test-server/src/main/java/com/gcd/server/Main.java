package com.gcd.server;

import com.gcd.rpc.transmission.RpcServer;
import com.gcd.rpc.transmission.socket.server.SocketRpcServer;

public class Main {
    public static void main(String[] args) {
        RpcServer server=new SocketRpcServer(8888);
        server.start();
    }
}
