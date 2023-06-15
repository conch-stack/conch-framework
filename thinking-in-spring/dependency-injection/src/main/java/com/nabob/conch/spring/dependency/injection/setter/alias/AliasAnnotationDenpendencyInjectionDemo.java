package ltd.beihu.spring.dependency.injection.setter.alias;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Map;

/**
 * Annotation 方式注入
 *
 * @author Adam
 * @date 2020/4/10
 */
public class AliasAnnotationDenpendencyInjectionDemo {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AliasAnnotationDenpendencyInjectionDemo.class);

        applicationContext.refresh();

        ObjectProvider<AliasInterface> beanProvider = applicationContext.getBeanProvider(AliasInterface.class);
        beanProvider.stream().forEach(AliasInterface::print);

        System.out.println("------------------------------------------------------------------------");

        Map<String, AliasInterface> beansOfType = applicationContext.getBeansOfType(AliasInterface.class);
        beansOfType.values().forEach(AliasInterface::print);

        System.out.println("------------------------------------------------------------------------");


        String[] user2s = applicationContext.getAliases("user*");
        for (String user2 : user2s) {
            System.out.println(user2);
        }

        applicationContext.close();


    }

    @Bean({"user1", "user2"})
    public AliasInterface aliasInterface() {
        return new AliasInterfaceOne(String.valueOf(System.nanoTime()));
    }

    @Bean({"user3", "user4"})
    public AliasInterface aliasInterface1() {
        return new AliasInterfaceTwo(String.valueOf(System.nanoTime()));
    }
}
