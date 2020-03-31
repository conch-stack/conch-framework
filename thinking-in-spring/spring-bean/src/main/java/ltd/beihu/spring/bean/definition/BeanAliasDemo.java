package ltd.beihu.spring.bean.definition;

import ltd.beihu.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 别名 查找依赖
 *
 * @author Adam
 * @since 2020/3/31
 */
public class BeanAliasDemo {

    public static void main(String[] args) {

        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:META-INF/bean-definition-context.xml");

        // 别名查找依赖
        User aliasUser = beanFactory.getBean("aliasUser", User.class);
        User user = beanFactory.getBean("user", User.class);
        System.out.println(aliasUser == user);

    }
}
