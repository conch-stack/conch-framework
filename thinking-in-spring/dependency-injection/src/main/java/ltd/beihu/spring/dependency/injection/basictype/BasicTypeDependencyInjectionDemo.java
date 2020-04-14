package ltd.beihu.spring.dependency.injection.basictype;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * @author Adam
 * @date 2020/4/14
 */
public class BasicTypeDependencyInjectionDemo {

    public static void main(String[] args) {

        // 创建BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // XML方式读取BeanDefinition
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:META-INF/basictype-dependency-injection.xml");

        ObjectProvider<UserTwo> beanProvider = beanFactory.getBeanProvider(UserTwo.class);
        UserTwo userTwo = beanProvider.getIfAvailable();
        System.out.println(userTwo.toString());
    }
}
