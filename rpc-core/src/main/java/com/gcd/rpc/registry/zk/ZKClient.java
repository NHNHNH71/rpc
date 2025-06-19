package com.gcd.rpc.registry.zk;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import com.gcd.rpc.constant.RpcConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private final CuratorFramework client;
    //客户端获取服务节点时使用cache快速查找 不用每次都找zk
    private final Map<String,List<String>> SERVICE_ADDRESS_CACHE=new ConcurrentHashMap<>();
    //新增节点时 服务器使用set判断节点是否已存在
    private final Set<String> SERVICE_ADDRESS_SET= ConcurrentHashMap.newKeySet();

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
    public void clearAllService(InetSocketAddress address){
        if(Objects.isNull(address)) throw new IllegalArgumentException("address不能为空");
        String endStr=address.getAddress().getHostAddress()+StrUtil.COLON+address.getPort();
        SERVICE_ADDRESS_SET.forEach(path->{
            if(path.endsWith(endStr)){
                log.info("{}服务已下线，删除",path);
                try {
                    client.delete()
                            .deletingChildrenIfNeeded()
                            .forPath(path);
                } catch (Exception e) {
                    log.info("{}服务下线zk删除失败",path);
                }
            }
        });
    }
    @SneakyThrows
    public void createPersistentNode(String path)  {
        log.info("新建节点名：{}",path);
        if(StrUtil.isBlank(path)) throw new IllegalArgumentException("path为空");
        if(SERVICE_ADDRESS_SET.contains(path)) {
            log.info("该节点已存在");
            return ;
        }
        if(client.checkExists().forPath(path)!=null){
            SERVICE_ADDRESS_SET.add(path);
            log.info("该节点已存在");
            return ;
        }
        log.info("开始创建节点");
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
        SERVICE_ADDRESS_SET.add(path);
    }
    @SneakyThrows
    public List<String> getChildrenNodes(String path) {
        if(StrUtil.isBlank(path)) throw new IllegalArgumentException("path为空");
        if(SERVICE_ADDRESS_CACHE.containsKey(path)) return SERVICE_ADDRESS_CACHE.get(path);
        List<String> nodes= client.getChildren().forPath(path);
        SERVICE_ADDRESS_CACHE.put(path,nodes);
        watchNode(path);
        return nodes;
    }
    private void watchNode(String path) throws Exception {
        PathChildrenCache pathChildrenCache=new PathChildrenCache(client,path,true);
        PathChildrenCacheListener pathChildrenCacheListener=new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                List<String> children=curatorFramework.getChildren().forPath(path);
                log.info("看看这是什么线程");
                children.forEach(s -> {
                    log.info("{}",s);
                });
                SERVICE_ADDRESS_CACHE.put(path,children);
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }
}
