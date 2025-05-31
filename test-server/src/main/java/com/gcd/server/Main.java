package com.gcd.server;

import com.gcd.rpc.transmission.RpcServer;

public class Main {
    public static void main(String[] args) {
        RpcServer server=new RpcServer() {
            @Override
            public void start() {

            }
        };
        server.start();
    }
}
