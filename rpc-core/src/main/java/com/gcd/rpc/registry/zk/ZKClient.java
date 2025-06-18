package com.gcd.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import com.gcd.rpc.constant.RpcConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @author nhnhnh7171
 * @Date 2025/6/18
 */
@Slf4j
public class ZKClient {
    //重试之间等待的初始时间
    private static final int BASE_SLEEP_TIME=1000;
    //最大重试次数
    private static final int MAX_RETRIES=3;
    private CuratorFramework client;
    public ZKClient(){
        this(RpcConstant.ZK_IP,RpcConstant.ZK_PORT);
    }
    public ZKClient(String hostname,int port){
        //重试策略
        RetryPolicy retryPolicy=new ExponentialBackoffRetry(BASE_SLEEP_TIME,MAX_RETRIES);
        this.client= CuratorFrameworkFactory.builder()
                .connectString(hostname+ StrUtil.COLON +port)
                .retryPolicy(retryPolicy)
                .build();
        log.info("zk开始链接..");
        this.client.start();
        log.info("zk链接成功");
    }
    @SneakyThrows
    public void createPersistentNode(String path)  {
        log.info("新建节点名：{}",path);
        if(StrUtil.isBlank(path)) throw new IllegalArgumentException("path为空");
        if(client.checkExists().forPath(path)!=null){
            log.info("该节点已存在");
            return ;
        }
        log.info("开始创建节点");
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
    }
    @SneakyThrows
    public List<String> getChildrenNodes(String path) {
        if(StrUtil.isBlank(path)) throw new IllegalArgumentException("path为空");
        return client.getChildren().forPath(path);
    }
}
