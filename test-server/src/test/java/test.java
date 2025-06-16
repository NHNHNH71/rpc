import com.gcd.rpc.config.RpcServiceConfig;
import com.gcd.server.Service.UserServiceImpl;

/**
 * @author nhnhnh7171
 * @Date 2025/6/16
 */
public class test {
    public static void main(String[] args) {
        UserServiceImpl userServiceImpl=new UserServiceImpl();
        RpcServiceConfig config=new RpcServiceConfig("1.1.1","common",userServiceImpl);
        System.out.println(config.rpcServiceNames());
    }
}
