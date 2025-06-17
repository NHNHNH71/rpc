package com.gcd.client;

import com.gcd.api.User;
import com.gcd.api.UserService;
import com.gcd.client.utils.ProxyUtils;

public class Main {
    public static void main(String[] args) {
        UserService userService= ProxyUtils.getProxy(UserService.class);
        User u=userService.getUser(1L);
        System.out.println(u);
    }

}
