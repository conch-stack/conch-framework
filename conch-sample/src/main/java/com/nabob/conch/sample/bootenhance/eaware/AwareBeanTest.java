package com.nabob.conch.sample.bootenhance.eaware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * @author Adam
 * @since 2024/8/8
 */
@Component
public class AwareBeanTest implements BeanNameAware, ApplicationContextAware, BeanFactoryAware, BeanClassLoaderAware,
        EnvironmentAware, ServletConfigAware, ServletContextAware, ResourceLoaderAware, ApplicationEventPublisherAware, EmbeddedValueResolverAware,
        MessageSourceAware {

    @Override
    public void setBeanName(String name) {
        System.out.println("------------AwareBeanTest # setBeanName 开始-------------");
        System.out.println("[AwareBeanTest]  扩展点演示 # setBeanName name: " + name);
        System.out.println("------------AwareBeanTest # setBeanName 结束-------------");
        System.out.println();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {

    }

    @Override
    public void setEnvironment(Environment environment) {

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {

    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {

    }

    @Override
    public void setServletConfig(ServletConfig servletConfig) {

    }

    @Override
    public void setServletContext(ServletContext servletContext) {

    }
}
