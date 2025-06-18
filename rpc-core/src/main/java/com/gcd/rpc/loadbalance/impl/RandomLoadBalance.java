package com.gcd.rpc.loadbalance.impl;

import cn.hutool.core.util.RandomUtil;
import com.gcd.rpc.loadbalance.LoadBalance;

import java.util.List;

/**
 * @author nhnhnh7171
 * @Date 2025/6/18
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> list) {
        return RandomUtil.randomEle(list);
    }
}
