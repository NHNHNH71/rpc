package com.gcd.client;

import com.gcd.api.User;
import com.gcd.api.UserService;
import com.gcd.client.utils.ProxyUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Slf4j
public class Main {
    public static void main(String[] args) {
        UserService userService= ProxyUtils.getProxy(UserService.class);
        ExecutorService executorService= Executors.newFixedThreadPool(10);
        for(long i=0;i<10;i++){
            long finalI = i;
            executorService.execute(()->{
                User u=userService.getUser(finalI);
                log.info("当前线程获取结果:{}",u);
            });
        }
    }

}
