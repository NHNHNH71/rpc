package com.gcd.rpc.util;

import cn.hutool.core.util.StrUtil;

import java.net.InetSocketAddress;

/**
 * @author nhnhnh7171
 * @Date 2025/6/18
 */
public class IPUtils {

    public static InetSocketAddress toInetSocketAddress(String address){
        if(StrUtil.isBlank(address)) throw new IllegalArgumentException("address不能为空");
        String[] split=address.split(":");
        if(split.length!=2) throw new IllegalArgumentException("address格式错误，address:"+address);
        return new InetSocketAddress(split[0],Integer.parseInt(split[1]));
    }
}
