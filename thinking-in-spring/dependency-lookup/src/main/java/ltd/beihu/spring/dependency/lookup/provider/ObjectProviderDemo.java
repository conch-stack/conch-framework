package ltd.beihu.spring.dependency.lookup.provider;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 单一类型依赖查找： BeanFactory
 *   1. 按照Bean的名称查找
 *   2. 按照Bean的类型查找
 *          实时
 *          延迟：
 *              ObjectFactory
 *              ObjectProvider （Spring 5.1）
 *   3. 按照Bean名称+类型查找
 *
 *
 * @author Adam
 * @date 2020/4/4
 */
public class ObjectProviderDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        applicationContext.register(ObjectProviderDemo.class);
        applicationContext.refresh();

        // 延迟加载 继承自  ObjectFactory
        ObjectProvider<String> beanProvider = applicationContext.getBeanProvider(String.class);
        System.out.println(beanProvider.getObject());

        System.out.println(beanProvider.getIfAvailable());
        System.out.println(beanProvider.getIfAvailable(String::new));

        System.out.println("unique: " + beanProvider.getIfUnique());
        
        // 流处理
        System.out.println(beanProvider.stream().count());
        beanProvider.stream().forEach(System.out::println);

        // TODO ObjectProvider<T> getBeanProvider(ResolvableType requiredType)  处理泛型 多类型方式

        applicationContext.close();
    }

    @Bean
    @Primary
    public String helloBean() {
        return "hello, world";
    }

    @Bean
    public String WorldBean() {
        return "xxxxxx";
    }
}
