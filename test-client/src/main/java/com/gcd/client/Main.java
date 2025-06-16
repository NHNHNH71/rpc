package com.gcd.client;

import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.transmission.RpcClient;
import com.gcd.rpc.transmission.socket.client.SocketRpcClient;

public class Main {
    public static void main(String[] args) {
        RpcClient client=new SocketRpcClient("127.0.0.1",8888);
        RpcReq req = RpcReq.builder()
                .reqId("123")
                .interfaceName("com.gcd.api.UserService")
                //.version("1.0.0")
                //.group("common")
                .methodName("getUser")
                .params(new Object[]{1L})
                .paramTypes(new Class[]{Long.class})
                .build();
        RpcResp<?> resp = client.sendReq(req);
        System.out.println(resp.getData());
    }
}
