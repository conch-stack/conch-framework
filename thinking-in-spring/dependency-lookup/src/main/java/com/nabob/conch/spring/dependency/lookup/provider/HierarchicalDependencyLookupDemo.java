package ltd.beihu.spring.dependency.lookup.provider;

import ltd.beihu.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

/**
 * 层次 依赖查找
 *
 * @author Adam
 * @date 2020/4/5
 */
public class HierarchicalDependencyLookupDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(HierarchicalDependencyLookupDemo.class);

        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        System.out.println("当前 BeanFactory 的 Parent BeanFactory： " + beanFactory.getParentBeanFactory());

        // 设置父 BeanFactory
        BeanFactory parentBeanFactory = createBeanFactory();
        beanFactory.setParentBeanFactory(parentBeanFactory);
        System.out.println("当前 BeanFactory 的 Parent BeanFactory： " + beanFactory.getParentBeanFactory());

        // false
        displayLocalBean(beanFactory, "user");
        // true
        displayLocalBean((HierarchicalBeanFactory) parentBeanFactory, "user");

        // true
        displayContainsBean(beanFactory, "user");

        /**
         * BeanFactoryUtils
         */
        // 单一类型 （报错：NoSuchBeanDefinitionException ： beanOfType不会进行双亲委派查询）
//        User user = BeanFactoryUtils.beanOfType(beanFactory, User.class);
//        System.out.println("BeanFactoryUtils:单一类型:" + user);
        // 集合类型 （内部 递归 查找）
        Map<String, User> stringUserMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, User.class);
        System.out.println("BeanFactoryUtils: 集合类型 （内部 递归 查找）:" + stringUserMap);

        applicationContext.refresh();

        applicationContext.close();
    }

    /**
     * 查找 Bean 双亲委派 递归
     *
     * @param beanFactory
     * @param beanName
     * @return
     */
    private static boolean containsBean(HierarchicalBeanFactory beanFactory, String beanName) {
        BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
        if (parentBeanFactory instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory parentHierarchicalBeanFactory = (HierarchicalBeanFactory) parentBeanFactory;
            if (containsBean(parentHierarchicalBeanFactory, beanName)) {
                return true;
            }
        }
        return beanFactory.containsLocalBean(beanName);
    }

    /**
     * 展示 Bean 双亲委派 全上下文查找
     *
     * @param beanFactory
     * @param beanName
     */
    private static void displayContainsBean(HierarchicalBeanFactory beanFactory, String beanName) {
        System.out.printf("当前 BeanFactory [%s] 是否包含 bean [name : %s] : ContainsBean: %s\n", beanFactory, beanName,
                containsBean(beanFactory, beanName));
    }

    /**
     * 简单展示 LocalBean
     *
     * @param beanFactory
     * @param beanName
     */
    private static void displayLocalBean(HierarchicalBeanFactory beanFactory, String beanName) {
        System.out.printf("当前 BeanFactory [%s] 是否包含 Bean [name : %s] : LocalBean: %s\n", beanFactory, beanName,
                beanFactory.containsLocalBean(beanName));
    }


    /**
     * 构建 BeanFactory
     *
     * @return
     */
    private static BeanFactory createBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:/META-INF/dependency-lookup-context.xml");
        return beanFactory;
    }
}
