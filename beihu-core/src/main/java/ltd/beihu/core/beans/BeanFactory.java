package ltd.beihu.core.beans;

/**
 * @Author: zjz
 * @Desc: Bean工厂
 * @Date: 2019/10/27
 * @Version: V1.0.0
 */
public interface BeanFactory {

    /**
     * 获取Bean实例
     */
    Object getBean(String beanName) throws Exception;
}
