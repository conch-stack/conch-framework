package ltd.beihu.spring.dependency.injection.collectiontype;

import ltd.beihu.spring.dependency.injection.setter.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Adam
 * @since 2021/10/9
 */
public class MapCollectionTypeDependencyInjectionDemo {

    @Resource
    private Map<String, UserInfoInterface> userInfoInterfaceMap;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(MapCollectionTypeDependencyInjectionDemo.class);

        applicationContext.refresh();

        MapCollectionTypeDependencyInjectionDemo bean = applicationContext.getBean(MapCollectionTypeDependencyInjectionDemo.class);
        Map<String, UserInfoInterface> userInfoInterfaceMap = bean.getUserInfoInterfaceMap();
        userInfoInterfaceMap.forEach((k, v) -> {
            System.out.println(k);
            v.print();
        });


        applicationContext.close();

    }

    @Bean
    public UserInfoInterface1 getUserInfoInterface1() {
        return new UserInfoInterface1();
    }

    @Bean
    public UserInfoInterface2 getUserInfoInterface2() {
        return new UserInfoInterface2();
    }

    public Map<String, UserInfoInterface> getUserInfoInterfaceMap() {
        return userInfoInterfaceMap;
    }
}
