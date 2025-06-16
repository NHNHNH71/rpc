package com.gcd.server.Service;

import com.gcd.api.User;
import com.gcd.api.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(Long id) {
        System.out.println("getUser方法被调用");
        return User.builder()
                .id(id)
                .name("hello rpc")
                .build();
    }
}
