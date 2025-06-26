package com.gcd.api;

import com.gcd.rpc.annotation.Breaker;
import com.gcd.rpc.annotation.Limit;
import com.gcd.rpc.annotation.Retry;

public interface UserService {
    //@Retry
    @Breaker(windowTime = 30000)
    public User getUser(Long id);
}
