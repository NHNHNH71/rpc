import cn.hutool.core.util.IdUtil;
import com.gcd.rpc.dto.RpcReq;
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
        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 8888);
        System.out.println(address.getAddress().getHostAddress());
    }
}
