package com.gcd.rpc.util;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author nhnhnh7171
 * @Date 2025/6/17
 */
@Slf4j
public final class ThreadPoolUtils {
    private static final Map<String, ExecutorService> THREAD_POOL_CACHE=new
            ConcurrentHashMap<>();
    //获取当前设备cpu数量
    private static final int CPU_NUM=Runtime.getRuntime().availableProcessors();
    //CPU密集型任务的核心线程数
    private static final int CPU_INTENSIVE_NUM=CPU_NUM+1;
    //IO密集型
    private static final int IO_INTENSIVE_NUM=CPU_NUM*2;
    //线程池默认参数
    private static final int DEFAULT_KEEP_ALIVE_TIME=60;
    private static final int DEFAULT_QUEUE_SIZE=128;


    public static ExecutorService createCpuIntensiveThreadPoll(
            String pollName
    ){
        return createThreadPool(CPU_INTENSIVE_NUM,pollName);
    }
    public static ExecutorService createIoIntensiveThreadPoll(
            String pollName
    ){
        return createThreadPool(IO_INTENSIVE_NUM,pollName);
    }
    public static ExecutorService createThreadPool(
            int corePollSize,
            String pollName
    ){
        return createThreadPool(corePollSize,corePollSize,pollName);
    }
    public static ExecutorService createThreadPool(
            int corePollSize,
            int maxPollSize,
            String pollName
    ){
        return createThreadPool(corePollSize,maxPollSize, DEFAULT_KEEP_ALIVE_TIME,DEFAULT_QUEUE_SIZE,pollName,false);
    }
    public static ExecutorService createThreadPool(
            int corePollSize,
            int maxPollSize,
            long keepAliveTime,
            int queueSize,
            String pollName,
            boolean isDaemon
    ){
        if(THREAD_POOL_CACHE.containsKey(pollName)){
            return THREAD_POOL_CACHE.get(pollName);
        }
        ExecutorService executorService= new ThreadPoolExecutor(
                corePollSize,
                maxPollSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize),
                createThreadFactory(pollName,isDaemon)
        );
        THREAD_POOL_CACHE.put(pollName,executorService);
        return executorService;
    }
    public static ThreadFactory createThreadFactory(String pollName){
        //默认不设置守护线程
        return createThreadFactory(pollName,false);
    }
    public static ThreadFactory createThreadFactory(String pollName,boolean isDaemon){
        //isDaemon判断是否需要守护线程
        ThreadFactoryBuilder threadFactoryBuilder = ThreadFactoryBuilder.create()
                .setDaemon(isDaemon);
        if(StrUtil.isBlank(pollName)){
            return threadFactoryBuilder.build();
        }
        return threadFactoryBuilder.setNamePrefix(pollName).build();
    }
    public static void shutDownAll(){
        THREAD_POOL_CACHE.entrySet().parallelStream()
                .forEach(entry->{
                    String poolName=entry.getKey();
                    ExecutorService executorService=entry.getValue();
                    log.info("{},线程池开始关闭",executorService);
                    executorService.shutdown();
                    try {
                        if(executorService.awaitTermination(10,TimeUnit.SECONDS)){
                            log.info("{},线程池关闭成功",poolName);
                        }else{
                            log.info("{},线程池10秒内未关闭，开始强制关闭",poolName);
                            executorService.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        log.error("{},线程池停止异常",poolName);
                        executorService.shutdownNow();
                    }
                });
    }
}
