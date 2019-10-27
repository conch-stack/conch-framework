package ltd.beihu.core.beans;

/**
 * @Author: zjz
 * @Desc: 注册Bean定义 即: BeanDefinition
 * @Date: 2019/10/27
 * @Version: V1.0.0
 */
public interface BeanDefinitionRegistry {

    /**
     * 注册
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws Exception;

    /**
     * 获取 BeanDefinition
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * check 是否存在 BeanDefinition
     */
    boolean containsBeanDefinition(String beanName);
}
