package com.gcd.rpc.transmission.socket.client;

import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.transmission.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author nhnhnh7171
 * @Date 2025/5/31
 */
@Slf4j
public class SocketRpcClient implements RpcClient {
    private final String host;
    private final int port;
    public SocketRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public RpcResp<?> sendReq(RpcReq req) {
        //通过socket与服务端建立连接
        try (Socket socket=new Socket(host,port)) {
            //讲req数据发送到服务器
            ObjectOutputStream objectOutputStream = new ObjectOutputStream( socket.getOutputStream());
            objectOutputStream.writeObject(req);
            objectOutputStream.flush();
            //从服务端拿取数据即获取服务端响应
            ObjectInputStream inputStream = new ObjectInputStream( socket.getInputStream());
            Object o = inputStream.readObject();
            return (RpcResp<?>) o;

        }catch (Exception e){
            log.error("发送rpc请求失败",e);
        }
        return null;
    }
}
