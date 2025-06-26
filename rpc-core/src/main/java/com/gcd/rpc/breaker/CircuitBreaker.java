package com.gcd.rpc.breaker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nhnhnh7171
 * @Date 2025/6/26
 */
public class CircuitBreaker {
    //熔断器状态
    private State state=State.CLOSED;
    //成功次数
    private final AtomicInteger successCount=new AtomicInteger();
    //失败次数
    private final AtomicInteger failCount=new AtomicInteger();
    //总次数
    private final AtomicInteger total=new AtomicInteger();
    //失败阈值 失败了多少次则进入close状态
    private final int failThreshold;
    //成功比率 即半开状态需要多大的成功比例才能close
    private final double successRateInHalfOpen;
    //熔断时间窗口 即一次close的持续时间
    private final long windowTime;
    //上一次失败的时间
    private long lastFailTime=0;

    public CircuitBreaker(int failThreshold, double successRateInHalfOpen, long windowTime) {
        this.failThreshold = failThreshold;
        this.successRateInHalfOpen = successRateInHalfOpen;
        this.windowTime = windowTime;
    }
    //消息发送失败
    public synchronized void fail(){
        failCount.incrementAndGet();
        lastFailTime=System.currentTimeMillis();
        if(state==State.HALF_OPEN) {
            state=State.OPEN;
            return;
        }
        if(failCount.get()>failThreshold) state=State.OPEN;
    }
    //消息发送成功
    public synchronized void success(){
        if(state==State.CLOSED){
            resetCount();
            return ;
        }
        successCount.incrementAndGet();
        if(successCount.get()>successRateInHalfOpen*total.get()){
            state=State.CLOSED;
            resetCount();
        }
    }
    //判断是否可以发送消息
    public synchronized boolean canSendReq(){
        switch (state){
            case OPEN:
                if(System.currentTimeMillis()-lastFailTime<=windowTime){
                    return false;
                }
                resetCount();
                total.incrementAndGet();
                state=State.HALF_OPEN;
                return true;
            case CLOSED:
                return true;
            case HALF_OPEN:
                total.incrementAndGet();
                return true;
            default:
                throw new IllegalArgumentException("熔断器状态state异常");
        }
    }
    private void resetCount(){
        successCount.set(0);
        failCount.set(0);
        total.set(0);
    }

    enum State{
        CLOSED,
        OPEN,
        HALF_OPEN,
    }
}
