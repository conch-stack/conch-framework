package ltd.beihu.sample.job;

import ltd.beihu.sample.uitl.BeanDefinitionUtil;
import ltd.beihu.sample.uitl.ReflectionHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Adam
 * @since 2022/11/15
 */
public class Config implements ApplicationContextAware, BeanFactoryAware, InitializingBean {

    private ApplicationContext context;
    private ConfigurableListableBeanFactory beanFactory;

    // <topic,反射配置Bean>
    private Map<String, ConfigBean> configMap = new HashMap<>();

    public Map<String, ConfigBean> getConfigMap() {
        return configMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (String name : context.getBeanDefinitionNames()) {
            Object bean = beanFactory.getBean(name);
            Class<?> beanClassType = BeanDefinitionUtil.resolveBeanClassType(beanFactory.getBeanDefinition(name));
            Service service = beanClassType.getAnnotation(Service.class);
            System.out.println("==============================================" + beanClassType.isAnnotationPresent(Service.class));
            System.out.println("==============================================" + beanClassType.isAnnotationPresent(Component.class));
            if (Objects.nonNull(service)) {
                Method[] allDeclaredMethods = ReflectionHelper.getAllDeclaredMethods(beanClassType);
                Arrays.stream(allDeclaredMethods).forEach(target -> {
                    QmqConsumer qmqConsumer = AnnotationUtils.findAnnotation(target, QmqConsumer.class);
                    if (Objects.nonNull(qmqConsumer)) {
                        configMap.put(qmqConsumer.topic(), new ConfigBean(bean, target));
                    }
                });
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
