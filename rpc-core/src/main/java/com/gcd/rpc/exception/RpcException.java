package com.gcd.rpc.exception;

/**
 * @author nhnhnh7171
 * @Date 2025/6/17
 */
public class RpcException extends RuntimeException{
    public RpcException(){
        super();
    }
    public RpcException(String message){
        super(message);
    }
    public RpcException(String message,Throwable cause){
        super(message,cause);
    }
    public RpcException(Throwable cause){
        super(cause);
    }
}
