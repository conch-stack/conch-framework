package ltd.beihu.sample.uitl;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class BeanDefinitionUtil {
    public BeanDefinitionUtil() {
    }

    public static Class<?> resolveBeanClassType(BeanDefinition beanDefinition) {
        Class<?> clazz = null;
        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            String className;
            if (isFromConfigurationSource(beanDefinition)) {
                MethodMetadata methodMetadata = ((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata();
                className = methodMetadata.getReturnTypeName();
            } else {
                AnnotationMetadata annotationMetadata = ((AnnotatedBeanDefinition) beanDefinition).getMetadata();
                className = annotationMetadata.getClassName();
            }

            try {
                clazz = StringUtils.isEmpty(className) ? null : ClassUtils.forName(className, (ClassLoader) null);
            } catch (Throwable var6) {
            }
        }

        if (clazz == null) {
            try {
                clazz = ((AbstractBeanDefinition) beanDefinition).getBeanClass();
            } catch (IllegalStateException var5) {
                try {
                    String className = beanDefinition.getBeanClassName();
                    clazz = StringUtils.isEmpty(className) ? null : ClassUtils.forName(className, (ClassLoader) null);
                } catch (Throwable var4) {
                }
            }
        }

        return ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;
    }

    public static boolean isFromConfigurationSource(BeanDefinition beanDefinition) {
        return beanDefinition.getClass().getCanonicalName().startsWith("org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader");
    }
}
