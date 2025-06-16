import com.gcd.api.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author nhnhnh7171
 * @Date 2025/6/16
 */
public class test {
    public static void main(String[] args) {
        List<User> list=new ArrayList<>();
        list.add(new User(1L,"张三"));
        list.add(new User(2L,"里斯"));
        list.add(new User(3L,"张飞"));
        list.add(new User(4L,"李四"));
        list.add(new User(5L,"里面"));
        list.add(new User(6L,"张三"));
        //List<Long> u=list.stream().filter(user -> user.getName().startsWith("张")).map(User::getId).collect(Collectors.toList());
//        Optional<User> optional=list.stream().max(new Comparator<User>(){
//            @Override
//            public int compare(User u1,User u2){
//                return (int)(u2.getId()-u1.getId());
//            }
//        });
        List<User> l=list.stream().peek(user -> user.setId(user.getId()+1L)).collect(Collectors.toList());
        l.get(0).setName("zhangsi");
        System.out.println(list.get(0).getName());
    }
}
