package com.gcd.server.Service;

import com.gcd.api.User;
import com.gcd.api.UserService;
import com.gcd.rpc.annotation.Limit;
import com.gcd.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
public class UserServiceImpl implements UserService {
    private static final AtomicInteger ID_INCREASE=new AtomicInteger();
    //@Limit(requestsPerSecond = 2,timeout = 0)
    @Override
    public User getUser(Long id) {
        // 根据不同的id抛出不同类型的异常
        if (id == 1L) {
            throw new RpcException("RPC调用异常测试");
        }
        if (id == 2L) {
            throw new RuntimeException("运行时异常测试");
        }
        if (id == 3L) {
            throw new IllegalArgumentException("参数异常测试");
        }
        if (id == 4L) {
            throw new NullPointerException("空指针异常测试");
        }
        
//        // 随机异常测试
//        Random random = new Random();
//        int randomValue = random.nextInt(100);
//
//        if (randomValue < 20) {
//            throw new RpcException("随机RPC异常-概率20%");
//        } else if (randomValue < 40) {
//            throw new RuntimeException("随机运行时异常-概率20%");
//        }
//        long a=(long)ID_INCREASE.incrementAndGet();
//        log.info("a的值为:{}",a);
//        if(a%3!=2) {
//            log.info("this is a test");
//            throw new IllegalArgumentException("参数异常测试");
//        }
        
        // 正常返回逻辑
        System.out.println("getUser方法被调用，id: " + id);
        return User.builder()
                .id(id)
                .name("hello rpc")
                .build();
    }
}
