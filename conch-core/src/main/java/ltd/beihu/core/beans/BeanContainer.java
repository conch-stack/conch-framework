package ltd.beihu.core.beans;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.beihu.core.beans.annotation.Component;
import ltd.beihu.core.beans.annotation.Controller;
import ltd.beihu.core.beans.annotation.Repository;
import ltd.beihu.core.beans.annotation.Service;
import ltd.beihu.core.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Bean 容器
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContainer {

    /**
     * 存放所有 Bean 的 Map
     */
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 是否加载 Bean
     */
    private boolean isLoadBean = false;

    /**
     * 加载 bean 的注解列表
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION
            = Arrays.asList(Component.class, Controller.class, Service.class, Repository.class);

    /**
     * 获取 Bean 容器实例
     */
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    /**
     * 扫描加载所有 Bean
     */
    public void loadBeans(String basePackage) {
        if (isLoadBean()) {
            log.warn("bean 已经加载");
            return;
        }

        Set<Class<?>> classSet = ClassUtil.getPackageClass(basePackage);
        classSet.stream()
                .filter(clz -> {
                    for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                        if (clz.isAnnotationPresent(annotation)) {
                            return true;
                        }
                    }
                    return false;
                })
                .forEach(clz -> beanMap.put(clz, ClassUtil.newInstance(clz)));
        isLoadBean = true;
    }

    /**
     * 是否加载 Bean
     */
    public boolean isLoadBean() {
        return isLoadBean;
    }

    /**
     * 获取 Bean 实例
     */
    public Object getBean(Class<?> clz) {
        if (null == clz) {
            return null;
        }
        return beanMap.get(clz);
    }

    /**
     * 获取所有 Bean 集合
     */
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * 添加一个 Bean 实例
     */
    public Object addBean(Class<?> clz, Object bean) {
        return beanMap.put(clz, bean);
    }

    /**
     * 移除一个 Bean 实例
     */
    public void removeBean(Class<?> clz) {
        beanMap.remove(clz);
    }

    /**
     * Bean 实例数量
     */
    public int size() {
        return beanMap.size();
    }

    /**
     * 所有 Bean 的 Class 集合
     */
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    /**
     * 通过注解获取 Bean 的 Class 集合
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        return beanMap.keySet()
                .stream()
                .filter(clz -> clz.isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }

    /**
     * 通过实现类或者父类获取 Bean 的 Class 集合
     */
    public Set<Class<?>> getClassesBySuper(Class<?> superClass) {
        return beanMap.keySet()
                .stream()
                .filter(superClass::isAssignableFrom)
                .filter(clz -> !clz.equals(superClass))
                .collect(Collectors.toSet());
    }

    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;

        ContainerHolder() {
            instance = new BeanContainer();
        }
    }
}
