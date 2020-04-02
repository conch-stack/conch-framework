package ltd.beihu.spring.bean.definition;

import ltd.beihu.spring.bean.factory.UserFactoryBean;
import ltd.beihu.spring.bean.initialization.InitializationBean;
import ltd.beihu.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 注解 Bean Definition 示例
 *      注册Bean Definition
 *      实例化Bean
 *      初始化Bean
 *
 * @author Adam
 * @since 2020/3/31
 */
// 3. @Import定义
@Import(AnnotatedBeanDefinitonDemo.UserConfig.class)
public class AnnotatedBeanDefinitonDemo {

    public static void main(String[] args) {

        // 创建 ApplicationContext 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class
        applicationContext.register(AnnotatedBeanDefinitonDemo.class);

        // 启动应用上下文
        applicationContext.refresh();

        // API配置元信息注册
        // 1. 命名 Bean 的注册
        registerBeanDefinition(applicationContext, "testUser", User.class);
        registerBeanDefinition(applicationContext, User.class);
        // 2. 非命名 Bean 的注册

        // 注解注册
        Map<String, UserConfig> userConfigs = applicationContext.getBeansOfType(UserConfig.class);
        System.out.println("userConfigs: " + userConfigs);
        Map<String, User> users = applicationContext.getBeansOfType(User.class);
        System.out.println("users: " + users);

        // FactoryBean 实例化Bean
        User userByFactoryBean = applicationContext.getBean("user-by-factroy-bean", User.class);
        System.out.println("userByFactoryBean: " + userByFactoryBean);

        // 初始化Bean
        InitializationBean initializationBean = applicationContext.getBean("test-InitializationBean", InitializationBean.class);
        System.out.println("initializationBean: " + initializationBean);
        // 停止
        applicationContext.close();
    }

    public static void registerBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry, String beanName, Class<?> beanClazz) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
        beanDefinitionBuilder.addPropertyValue("age", 1);

        if (StringUtils.hasText(beanName)) {
            beanDefinitionBuilder.addPropertyValue("name", "register bean definiton with beanName");
            // 注册 BeanDefinition
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        } else {
            beanDefinitionBuilder.addPropertyValue("name", "register bean definiton with no beanName");
            BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinitionBuilder.getBeanDefinition(), beanDefinitionRegistry);
        }
    }

    public static void registerBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry, Class<?> beanClazz) {
        registerBeanDefinition(beanDefinitionRegistry, null, beanClazz);
    }

    // 2. @Component定义
    @Component
    public static class UserConfig {
        // 1. @Bean定义
        @Bean(name = {"user", "aliasUser"})
        public User user() {
            return new User("AnnotatedBeanDefinitonDemo", 10);
        }

        @Bean(name = "user-by-factroy-bean")
        public UserFactoryBean userFactoryBean() {
            return new UserFactoryBean();
        }

        @Bean(name = "test-InitializationBean", initMethod = "initMethod")
        public InitializationBean initializationBean() {
            return new InitializationBean();
        }
    }
}
