package ltd.beihu.spring.dependency.injection.qualifier;

import ltd.beihu.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Collection;

/**
 * @author Adam
 * @date 2020/4/14
 */
public class QualifierDependencyInjection {

    @Autowired
    private User user;

    /**
     * 指定名称
     */
    @Autowired
    @Qualifier("user")
    private User namedUser;

    @Autowired
    private Collection<User> allUsers;

    /**
     * Qualifier 注解分组
     */
    @Autowired
    @Qualifier
    private Collection<User> qualifiedUsers;

    /**
     * UserGroup 注解分组
     */
    @Autowired
    @UserGroup
    private Collection<User> userGroupUsers;

    // 注册Bean
    @Bean
    @Qualifier
    public User user1() {
        return buildUser(111);
    }

    @Bean
    @Qualifier
    public User user2() {
        return buildUser(222);
    }


    @Bean
    @UserGroup
    public User user3() {
        return buildUser(333);
    }

    @Bean
    @UserGroup
    public User user4() {
        return buildUser(444);
    }

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(QualifierDependencyInjection.class);

        // XML方式读取BeanDefinition
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:META-INF/dependency-lookup-context.xml");

        applicationContext.refresh();


        QualifierDependencyInjection bean = applicationContext.getBean(QualifierDependencyInjection.class);

        // 1
        System.out.println("user: [" + 1 + "] => "+ bean.user);
        // 1
        System.out.println("namedUser: [" + 1 + "] => " + bean.namedUser);
        // 2 加了@Qualifier之后，就被分组了，全部的就看不了他们的，所以别的用的地方必须同样 加上 @Qualifier
        System.out.println("allUsers: [" + bean.allUsers.size() + "] => " + bean.allUsers);
        // 4
        System.out.println("qualifiedUsers: [" + bean.qualifiedUsers.size() + "] => " + bean.qualifiedUsers);
        // 2
        System.out.println("userGroupUsers: [" + bean.userGroupUsers.size() + "] => " + bean.userGroupUsers);

        applicationContext.close();
    }

    private static User buildUser(Integer age) {
        User user = new User();
        user.setAge(age);
        return user;
    }
}
