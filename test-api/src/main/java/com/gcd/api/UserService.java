package com.gcd.api;

import com.gcd.rpc.annotation.Retry;

public interface UserService {
    @Retry
    public User getUser(Long id);
}
