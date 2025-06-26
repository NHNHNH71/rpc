import cn.hutool.core.util.IdUtil;
import com.gcd.rpc.dto.RpcReq;
import com.gcd.rpc.factory.SingletonFactory;
import com.gcd.rpc.serialize.Serializer;
import com.gcd.rpc.serialize.impl.HessianSerializer;
import com.gcd.rpc.serialize.impl.ProtostuffSerializer;
import com.gcd.rpc.transmission.socket.client.SocketRpcClient;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author nhnhnh7171
 * @Date 2025/5/31
 */
public class test {
    public static void main(String[] args) throws UnknownHostException {
        Serializer serializer= SingletonFactory.getInstance(ProtostuffSerializer.class);
        RpcReq req = RpcReq.builder()
                .reqId("felix71")
                .interfaceName("felixxxx")
                .paramTypes(new Class<?>[]{String.class, Integer.class})
                .group("yyy")
                .build();
        byte[] data=serializer.serialize(req);
        RpcReq deserializedReq=serializer.deserialize(data,RpcReq.class);
        System.out.println(deserializedReq);
    }
}
