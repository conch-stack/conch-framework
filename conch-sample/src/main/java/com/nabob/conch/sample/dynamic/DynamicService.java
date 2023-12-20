package com.nabob.conch.sample.dynamic;

import com.nabob.conch.sample.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * @author Adam
 * @since 2023/7/20
 */
@Service
public class DynamicService implements BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    public Collection<User> getUsers() {
        Map<String, User> beansOfType = beanFactory.getBeansOfType(User.class);
        return beansOfType.values();
    }
}
