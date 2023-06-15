package ltd.beihu.spring.dependency.injection.source.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

/**
 * 外部化配置 依赖来源
 *
 * @author Adam
 * @date 2020/4/25
 */
@Configuration
@PropertySource(value = "classpath:default.properties", encoding = "UTF-8")
public class ExternalConfigurationDependencySourceDemo {

    @Value("${user.id}")
    private Long id;

    @Value("${user.name}")
    private String name;

    @Value("${user.namez}")
    private String namez;

    @Value("${user.resource}")
    private Resource resource;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ExternalConfigurationDependencySourceDemo.class);

        applicationContext.refresh();

        ExternalConfigurationDependencySourceDemo bean = applicationContext.getBean(ExternalConfigurationDependencySourceDemo.class);

        System.out.println(bean.id);
        System.out.println("外部化配置具有优先级：" + bean.name);
        System.out.println("外部化配置具有优先级：" + bean.namez);
        System.out.println(bean.resource);

        applicationContext.close();

    }
}
