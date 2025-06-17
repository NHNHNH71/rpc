package com.gcd.rpc.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nhnhnh7171
 * @Date 2025/6/17
 */
@Slf4j
public class ShutdownHookUtils {
    public static void closeAll(){
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            log.info("系统结束运行，开始释放资源");
            ThreadPoolUtils.shutDownAll();
        }));
    }
}
