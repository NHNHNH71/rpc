package com.gcd.rpc.transmission.socket.server;

import com.gcd.rpc.config.RpcServiceConfig;
import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.dto.RpcResp;
import com.gcd.rpc.factory.SingletonFactory;
import com.gcd.rpc.handler.RpcReqHandler;
import com.gcd.rpc.provider.ServiceProvider;
import com.gcd.rpc.provider.impl.SimpleServiceProvider;
import com.gcd.rpc.provider.impl.ZKServiceProvider;
import com.gcd.rpc.transmission.RpcServer;
import com.gcd.rpc.transmission.socket.client.SocketRpcClient;
import com.gcd.rpc.util.ShutdownHookUtils;
import com.gcd.rpc.util.ThreadPoolUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * @author nhnhnh7171
 * @Date 2025/5/31
 */
@Slf4j
public class SocketRpcServer implements RpcServer {
    private final int port;
    private final ServiceProvider serviceProvider;
    private final RpcReqHandler rpcReqHandler;
    private final ExecutorService executorService;
    public SocketRpcServer(){
        this(RpcConstant.SERVER_PORT);
    }
    public SocketRpcServer(int port) {
        this(port, SingletonFactory.getInstance(ZKServiceProvider.class));
    }
    public SocketRpcServer(int port,ServiceProvider serviceProvider) {
        this.port = port;
        rpcReqHandler=new RpcReqHandler(serviceProvider);
        this.serviceProvider=serviceProvider;
        this.executorService= ThreadPoolUtils.createIoIntensiveThreadPoll("socket-rpc-server");
    }
    @Override
    public void start() {
        ShutdownHookUtils.addShutdownTask();
        log.info("start socket server");
        try (ServerSocket serverSocket = new ServerSocket(port)){
            log.info("socket创建成功，端口:{}",port);
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                executorService.submit(new SocketReqHandler(socket,rpcReqHandler));
            }
        } catch (Exception e) {
            log.error("服务端异常",e);
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }

}
