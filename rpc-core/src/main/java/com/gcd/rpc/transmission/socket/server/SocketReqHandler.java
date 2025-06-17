package com.gcd.rpc.transmission.socket.server;

import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.handler.RpcReqHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author nhnhnh7171
 * @Date 2025/6/17
 */
@Slf4j
public class SocketReqHandler implements Runnable{
    private final Socket socket;
    private final RpcReqHandler rpcReqHandler;

    public SocketReqHandler(Socket socket, RpcReqHandler rpcReqHandler) {
        this.socket = socket;
        this.rpcReqHandler = rpcReqHandler;
    }


    @SneakyThrows
    @Override
    public void run() {
        log.info("当前线程：{}",Thread.currentThread().getName());
        ObjectInputStream inputStream=new ObjectInputStream(socket.getInputStream());
        RpcReq req=(RpcReq)inputStream.readObject();

        //rpcReq中的接口实现类方法被调用
        System.out.println(req);
        Object data=rpcReqHandler.invoke(req);
        //返回结果
        ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
        RpcResp<?> rpcResp = RpcResp.success(req.getReqId(), data);
        outputStream.writeObject(rpcResp);
        outputStream.flush();
    }
}
