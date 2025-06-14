package com.gcd.rpc.transmission.socket.server;

import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.transmission.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author nhnhnh7171
 * @Date 2025/5/31
 */
@Slf4j
public class SocketRpcServer implements RpcServer {
    private final int port;
    public SocketRpcServer(int port) {
        this.port = port;
    }
    @Override
    public void start() {
        log.info("start socket server");
        try (ServerSocket serverSocket = new ServerSocket(port)){
            log.info("socket创建成功，端口:{}",port);
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                ObjectInputStream inputStream=new ObjectInputStream(socket.getInputStream());
                RpcReq req=(RpcReq)inputStream.readObject();

                //rpcReq中的接口实现类方法被调用
                System.out.println(req);
                String data="hello rpc";
                //返回结果
                ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
                RpcResp<String> rpcResp = RpcResp.success(req.getReqId(), data);
                outputStream.writeObject(rpcResp);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("服务端异常",e);
        }
    }
}
