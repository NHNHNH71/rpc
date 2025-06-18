import com.gcd.rpc.config.RpcServiceConfig;
import com.gcd.server.Service.UserServiceImpl;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author nhnhnh7171
 * @Date 2025/6/16
 */
public class test {
    //重试之间等待的初始时间
    private static final int BASE_SLEEP_TIME=1000;
    //最大重试次数
    private static final int MAX_RETRIES=3;
    public static void main(String[] args) throws Exception {
        //重试策略
        RetryPolicy retryPolicy=new ExponentialBackoffRetry(BASE_SLEEP_TIME,MAX_RETRIES);
        CuratorFramework zkClient= CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        String path="/node";
        NodeCache nodeCache=new NodeCache(zkClient,path);
        PathChildrenCache pathChildrenCache=new PathChildrenCache(zkClient,path,true);
        PathChildrenCacheListener pathChildrenCacheListener=new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if(pathChildrenCacheEvent.getType()== PathChildrenCacheEvent.Type.CHILD_REMOVED){
                    System.out.println("子节点被删除");
                }else if(pathChildrenCacheEvent.getType()== PathChildrenCacheEvent.Type.CHILD_ADDED){
                    System.out.println("子节点被添加");
                }else if(pathChildrenCacheEvent.getType()== PathChildrenCacheEvent.Type.CHILD_UPDATED){
                    System.out.println("子节点更新"+pathChildrenCacheEvent.getData());
                }
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
        System.in.read();
    }
}
