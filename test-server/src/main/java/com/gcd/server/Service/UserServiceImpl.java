package com.gcd.server.Service;

import com.gcd.api.User;
import com.gcd.api.UserService;
import com.gcd.rpc.exception.RpcException;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(Long id) {
        // 测试不同类型的异常
        if(id == 1L) {
            throw new RpcException("RPC自定义异常");
        } else if(id == 2L) {
            throw new RuntimeException("运行时异常");
        } else if(id == 3L) {
            throw new IllegalArgumentException("非法参数异常");
        } else if(id == 4L) {
            // 模拟空指针异常
            throw new NullPointerException("空指针异常");
        }

        System.out.println("getUser方法被调用");
        return User.builder()
                .id(id)
                .name("hello rpc")
                .build();
    }
}
