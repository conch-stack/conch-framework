package ltd.beihu.core.beans;

/**
 * @Author: zjz
 * @Desc: Bean定义：告诉Bean工厂，该如何创建某个类
 * @Date: 2019/10/27
 * @Version: V1.0.0
 */
public interface BeanDefinition {

    /**
     * 获取Bean的类名
     */
    Class<?> getBeanClass();

    /**
     * 获取工厂方法名（静态方法）
     */


    /**
     * 获取工厂Bean的名称（成员方法）
     */

    /**
     * 是否为单例
     */

    /**
     * 指定 初始化方法
     */
    String getInitMethodName();

    /**
     * 指定 特定销毁方法
     */
}
