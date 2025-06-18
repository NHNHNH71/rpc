package com.gcd.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.gcd.rpc.constant.RpcConstant;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.factory.SingletonFactory;
import com.gcd.rpc.loadbalance.LoadBalance;
import com.gcd.rpc.loadbalance.impl.RandomLoadBalance;
import com.gcd.rpc.registry.ServiceDiscovery;
import com.gcd.rpc.registry.zk.ZKClient;
import com.gcd.rpc.util.IPUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author nhnhnh7171
 * @Date 2025/6/18
 */
@Slf4j
public class ZKServiceDiscovery implements ServiceDiscovery {
    private final ZKClient zkClient;
    private final LoadBalance loadBalance;
    public ZKServiceDiscovery(){
        this(SingletonFactory.getInstance(ZKClient.class),SingletonFactory.getInstance(RandomLoadBalance.class));
    }
    public ZKServiceDiscovery(ZKClient client,LoadBalance loadBalance){
        this.zkClient=client;
        this.loadBalance=loadBalance;
    }
    @Override
    public InetSocketAddress lookupService(RpcReq req) {
        String rpcServiceName=req.rpcServiceName();
        String path= RpcConstant.ZK_RPC_ROOT_PATH+ StrUtil.SLASH
                +rpcServiceName;

        List<String> children=zkClient.getChildrenNodes(path);
        String address=loadBalance.select(children);
        log.info("找到服务节点path：{}",path+address);
        return IPUtils.toInetSocketAddress(address);
    }
}
