package com.gcd.client;

import ch.qos.logback.classic.Logger;
import com.gcd.api.User;
import com.gcd.api.UserService;
import com.gcd.client.utils.ProxyUtils;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.transmission.RpcClient;
import com.gcd.rpc.transmission.netty.RpcClient.NettyRpcClient;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Slf4j
public class Main {
    public static void main(String[] args) {
//        UserService userService= ProxyUtils.getProxy(UserService.class);
//        ExecutorService executorService= Executors.newFixedThreadPool(10);
//        for(int i=0;i<10;i++){
//            executorService.execute(()->{
//                User u=userService.getUser(1L);
//                log.info("当前线程获取结果:{}",u);
//            });
//        }
        RpcClient client=new NettyRpcClient();
        RpcResp<?> hell = client.sendReq(RpcReq.builder().interfaceName("hell").build());
    }

}
