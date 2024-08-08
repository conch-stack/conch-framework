package com.nabob.conch.sample.bootenhance.ainit;

import com.nabob.conch.sample.bootenhance.bbeanfactory.MyBeanDefinitionRegistryPostProcessor;
import com.nabob.conch.sample.bootenhance.bbeanfactory.MyBeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Iterator;

/**
 * ApplicationContextInitializer扩展点演示 时机: spring容器还没被刷新之前
 *
 * <p>
 * 因为这时候spring容器还没被刷新，所以想要自己的扩展的生效，有以下三种方式：
 * <p>
 *     Spring SPI扩展，在spring.factories中加入org.springframework.context.ApplicationContextInitializer=com.xzll.test.ApplicationContextInitializerPoint
 * <p>
 *
 * 这是整个spring容器在刷新之前初始化ConfigurableApplicationContext的回调接口，简单来说，就是在容器刷新之前调用此类的initialize方法。
 * 这个点允许被用户自己扩展。用户可以在整个spring容器还没被初始化之前做一些事情。
 * 可以想到的场景可能为，在最开始激活一些配置，或者利用这时候class还没被类加载器加载的时机，进行动态字节码注入等操作。
 */
public class ApplicationContextInitializerPoint implements ApplicationContextInitializer<ConfigurableApplicationContext> {

   @Override
   public void initialize(ConfigurableApplicationContext applicationContext) {
      System.out.println("------------ApplicationContextInitializerPoint # initialize 开始-------------");
      System.out.println("[ApplicationContextInitializer扩展点演示] # initialize:  " + applicationContext.toString());
      System.out.println("BeanDefinitionCount count: " + applicationContext.getBeanDefinitionCount());
      ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
      Iterator<String> beanNamesIterator = beanFactory.getBeanNamesIterator();
      beanNamesIterator.forEachRemaining(System.out::println);
      System.out.println("时机: "+ "run 方法中的 this.prepareContext(); 的时候");

//      applicationContext.addBeanFactoryPostProcessor();
//      applicationContext.addApplicationListener();

      applicationContext.addBeanFactoryPostProcessor(new MyBeanFactoryPostProcessor());
      applicationContext.addBeanFactoryPostProcessor(new MyBeanDefinitionRegistryPostProcessor());

      System.out.println("-------------ApplicationContextInitializerPoint # initialize 结束------------");
      System.out.println();
   }

}