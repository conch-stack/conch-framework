package ltd.beihu.spring.lifecycle.beanlifecycle;

import ltd.beihu.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Bean 元信息配置
 *
 * @author Adam
 * @date 2020/4/27
 */
public class BeanMetedataConfigurationDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanMetedataConfigurationDemo.class);
        // XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);

        // Properties 加载
        PropertiesBeanDefinitionReader propertiesBeanDefinitionReader = new PropertiesBeanDefinitionReader(applicationContext);
        int numDefinitions = propertiesBeanDefinitionReader.loadBeanDefinitions("classpath:user.properties");
        System.out.println("加载的BeanDefinition个数：" + numDefinitions);

        applicationContext.refresh();

        User user = applicationContext.getBean("user", User.class);
        System.out.println(user);

        applicationContext.close();
    }

}
