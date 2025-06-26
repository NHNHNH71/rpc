import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.factory.SingletonFactory;
import com.gcd.rpc.loadbalance.LoadBalance;
import com.gcd.rpc.loadbalance.impl.ConsistentHashLoadBalance;
import com.gcd.rpc.loadbalance.impl.RoundLoadBalance;
import com.gcd.rpc.serialize.Serializer;
import com.gcd.rpc.serialize.impl.HessianSerializer;
import com.gcd.rpc.serialize.impl.ProtostuffSerializer;
import com.gcd.rpc.transmission.socket.client.SocketRpcClient;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author nhnhnh7171
 * @Date 2025/5/31
 */
public class test {
    public static void main(String[] args) throws UnknownHostException {
        LoadBalance loadBalance=SingletonFactory.getInstance(ConsistentHashLoadBalance.class);
        List<String> list = ListUtil.of("ip1", "ip2", "ip3", "ip4");
        RpcReq req = RpcReq.builder()
                .interfaceName("interface")
                .group("group")
                .version("version")
                .methodName("method")
                .build();
        for(int i=0;i<8;i++){
            System.out.println(loadBalance.select(list,req));
        }
    }
}
