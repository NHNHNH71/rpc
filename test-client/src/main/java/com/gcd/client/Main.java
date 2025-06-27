package com.gcd.client;

import com.gcd.api.User;
import com.gcd.api.UserService;
import com.gcd.client.utils.ProxyUtils;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {
    public static void main(String[] args) {
        UserService userService= ProxyUtils.getProxy(UserService.class);
//        User u=userService.getUser(8L);
//        System.out.println(u);
        Scanner sc=new Scanner(System.in);

        ExecutorService executorService= Executors.newFixedThreadPool(10);
        int n=1;
        while (n!=0){
            System.out.println("请输入n");
            n=sc.nextInt();
            System.out.println("请输入id");
            long id=sc.nextLong();
            for(int i=0;i<n;i++){
                executorService.execute(()->{
                    User u=userService.getUser(id);
                    log.info("当前线程获取结果:{}",u);
                });
            }
        }
//        for(long i=7;i<11;i++){
////            User u=userService.getUser(i);
////            log.info("当前线程获取结果:{}",u);
//            long finalI = i;
//            executorService.execute(()->{
//                User u=userService.getUser(finalI);
//                log.info("当前线程获取结果:{}",u);
//            });
//        }
    }

}
