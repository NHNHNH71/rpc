package com.gcd.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.factory.SingletonFactory;
import com.gcd.rpc.registry.ServiceRegistry;
import com.gcd.rpc.registry.zk.ZKClient;
import com.gcd.rpc.util.ConfigUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author nhnhnh7171
 * @Date 2025/6/18
 */
@Slf4j
public class ZKServiceRegistry implements ServiceRegistry {
    private final ZKClient zkClient;
    public ZKServiceRegistry(){
        this(SingletonFactory.getInstance(ZKClient.class));
    }
    public ZKServiceRegistry(ZKClient client){
        this.zkClient=client;
    }
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress address) {
        if(Objects.isNull(address)) throw new IllegalArgumentException("address为空");
        log.info("服务注册开始，rpcServiceName: {},address: {}",rpcServiceName,address);
        String path= ConfigUtils.getRpcConfig().getZK_RPC_ROOT_PATH()+StrUtil.SLASH
                +rpcServiceName+StrUtil.SLASH
                +address.getAddress().getHostAddress()+ StrUtil.COLON+address.getPort();
        log.info("node节点为：{}",path);
        zkClient.createPersistentNode(path);
    }
    @SneakyThrows
    @Override
    public void clearAll() {
        String host= InetAddress.getLocalHost().getHostAddress();
        int port=ConfigUtils.getRpcConfig().getSERVER_PORT();
        zkClient.clearAllService(new InetSocketAddress(host,port));
    }
}
