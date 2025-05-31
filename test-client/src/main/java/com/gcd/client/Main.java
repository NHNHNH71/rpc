package com.gcd.client;

import com.gcd.api.User;
import com.gcd.api.UserService;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.transmission.RpcClient;

public class Main {
    public static void main(String[] args) {
        RpcClient client=new RpcClient() {
            @Override
            public RpcResp<?> sendReq(RpcReq req) {
                return null;
            }
        };
        RpcReq req = RpcReq.builder()
                .reqId("123")
                .interfaceName("com.gcd.api.UserService")
                .methodName("getUser")
                .params(new Object[]{1L})
                .paramTypes(new Class[]{Long.class})
                .build();
        RpcResp<?> resp = client.sendReq(req);
        User user=(User)resp.getData();
        System.out.println(user);
    }
}
