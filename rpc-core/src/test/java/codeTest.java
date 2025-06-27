import com.gcd.rpc.serialize.Serializer;
import com.gcd.rpc.spi.CustomLoader;

/**
 * @author nhnhnh7171
 * @Date 2025/6/16
 */
public class codeTest {
    public static void main(String[] args) {
        CustomLoader<Serializer> loader = CustomLoader.getLoader(Serializer.class);
        Serializer serializer=loader.get("protostuff4");
        System.out.println(serializer);
    }
}
