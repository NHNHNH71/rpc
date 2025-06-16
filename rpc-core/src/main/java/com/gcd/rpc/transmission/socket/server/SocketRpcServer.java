package com.gcd.rpc.transmission.socket.server;

import com.gcd.rpc.config.RpcServiceConfig;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.provider.ServiceProvider;
import com.gcd.rpc.provider.impl.SimpleServiceProvider;
import com.gcd.rpc.transmission.RpcServer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author nhnhnh7171
 * @Date 2025/5/31
 */
@Slf4j
public class SocketRpcServer implements RpcServer {
    private final int port;
    private final ServiceProvider serviceProvider;
    public SocketRpcServer(int port) {
        this(port,new SimpleServiceProvider());
    }
    public SocketRpcServer(int port,ServiceProvider serviceProvider) {
        this.port = port;
        this.serviceProvider=serviceProvider;
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
                Object data=invoke(req);
                //返回结果
                ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
                RpcResp<?> rpcResp = RpcResp.success(req.getReqId(), data);
                outputStream.writeObject(rpcResp);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("服务端异常",e);
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }
    //自动try catch并抛出异常，不用自己手动处理异常
    @SneakyThrows
    private Object invoke(RpcReq rpcReq)  {
        String rpcServiceName=rpcReq.rpcServiceName();
        System.out.println(rpcServiceName);
        Object service= serviceProvider.getService(rpcServiceName);
        log.info("获取到服务：{}",service.getClass().getCanonicalName());
        Method method=service.getClass().getMethod(rpcReq.getMethodName(),rpcReq.getParamTypes());
        log.info("获取到方法，开始反射");
        return method.invoke(service,rpcReq.getParams());
    }
}
