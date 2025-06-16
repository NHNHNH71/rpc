package com.gcd.rpc.transmission.socket.client;

import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.transmission.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
    //以下代码为解决粘包问题的代码
    public static void split(){
        ByteBuffer source=ByteBuffer.allocate(32);
        source.put("hello,world\ni'm zhangyi\nnihao".getBytes(StandardCharsets.UTF_8));
        splitt(source);
        source.put("woshishui\n".getBytes(StandardCharsets.UTF_8));
        splitt(source);

    }
    public static void splitt(ByteBuffer source){
        source.flip();
        for(int i=0;i<source.limit();i++){
            if(source.get(i)=='\n'){
                int length=i+1-source.position();
                ByteBuffer b=ByteBuffer.allocate(length);
                for(int j=0;j<length;j++){
                    b.put(source.get());
                }
                b.flip();
                System.out.println("111");
                while(b.hasRemaining()){

                    System.out.print((char)b.get());
                }
            }
        }
        source.compact();
    }
}
