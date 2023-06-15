package ltd.beihu.spring.ioc.overview.lookup;

import ltd.beihu.spring.ioc.overview.annotation.Super;
import ltd.beihu.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * 依赖查找
 *
 * @author Adam
 * @since 2020/3/30
 */
public class DependencyLookupDemo {

    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:META-INF/dependency-lookup-context.xml");
        // ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/META-INF/dependency-lookup-context.xml");

        // 按名字查找
        // lookupRealTime(beanFactory);
        // lookupLazy(beanFactory);

        // 按类型查找
         lookupByType(beanFactory);
         lookupCollectionType(beanFactory);

        // 按注解查找
        lookupByAnnotation(beanFactory);
    }

    private static void lookupRealTime(BeanFactory beanFactory) {
        User user = (User) beanFactory.getBean("user");
        System.out.println("实时查找：" + user);
    }

    private static void lookupLazy(BeanFactory beanFactory) {
        ObjectFactory<User> objectFactory = (ObjectFactory<User>) beanFactory.getBean("objectFactory");
        User user = objectFactory.getObject();
        System.out.println("延迟查找：" + user);
    }

    private static void lookupByType(BeanFactory beanFactory) {
        User user = beanFactory.getBean(User.class);
        System.out.println("按类型查找：" + user);
    }

    private static void lookupCollectionType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = listableBeanFactory.getBeansOfType(User.class);
            System.out.println("查找所有类型为User的Bean：" + users);
        }
    }

    private static void lookupByAnnotation(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = (Map) listableBeanFactory.getBeansWithAnnotation(Super.class);
            System.out.println("查找标注 @Super 的 User 集合对象：" + users);
        }
    }
}
