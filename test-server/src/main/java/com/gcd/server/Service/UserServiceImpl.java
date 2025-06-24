package com.gcd.server.Service;

import com.gcd.api.User;
import com.gcd.api.UserService;
import com.gcd.rpc.exception.RpcException;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(Long id) {
        if(id==1L) throw new RpcException("抛出异常");

        System.out.println("getUser方法被调用");
        return User.builder()
                .id(id)
                .name("hello rpc")
                .build();
    }
}
