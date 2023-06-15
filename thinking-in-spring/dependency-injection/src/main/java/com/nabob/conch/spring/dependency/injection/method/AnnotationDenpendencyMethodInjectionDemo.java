package ltd.beihu.spring.dependency.injection.method;

import ltd.beihu.spring.dependency.injection.setter.UserHolder;
import ltd.beihu.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Method 方式注入
 *
 * @author Adam
 * @date 2020/4/10
 */
public class AnnotationDenpendencyMethodInjectionDemo {

    private UserHolder userHolder;
    private UserHolder userHolder1;

    @Autowired
    public void initUserHolder(UserHolder userHolder) {
        this.userHolder = userHolder;
    }

    @Resource
    public void initUserHolder2(UserHolder userHolder1) {
        this.userHolder1 = userHolder1;
    }

    /**
     * Setter
     * @param user
     * @return
     */
    @Bean
    public UserHolder userHolder(User user) {
        UserHolder userHolder = new UserHolder();
        userHolder.setUser(user);
        return userHolder;
    }

    @PostConstruct
    public void test() {
        System.out.println("end");
    }

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AnnotationDenpendencyMethodInjectionDemo.class);

        // XML方式读取BeanDefinition
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:META-INF/dependency-lookup-context.xml");

        applicationContext.refresh();

        AnnotationDenpendencyMethodInjectionDemo propertyInjectionDemo = applicationContext.getBean(AnnotationDenpendencyMethodInjectionDemo.class);

        // @Autowired 自动关联
        System.out.println(propertyInjectionDemo.userHolder);
        // @Resource 自动关联
        System.out.println(propertyInjectionDemo.userHolder1);

        System.out.println(propertyInjectionDemo.userHolder == propertyInjectionDemo.userHolder1);

        applicationContext.close();
    }
}
