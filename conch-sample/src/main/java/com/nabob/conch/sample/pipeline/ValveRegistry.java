package com.nabob.conch.sample.pipeline;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Valve Registry
 *
 * @author Adam
 * @since 2023/9/21
 */
@Component
public class ValveRegistry implements InitializingBean, ApplicationContextAware {

    @Resource
    private List<Valve<?, ?>> valves;

    private static final Map<String, List<Valve>> VALVE_MAP = new HashMap<>(64);

    /**
     * Known spring application context object
     */
    private ApplicationContext applicationContext;

    public List<Valve<?, ?>> getValves() {
        return valves;
    }

    public <req, res> List<Valve<req, res>> getTargetValves(Class<req> reqClazz, Class<res> resClazz) {
//        for (Valve<?, ?> valve : getValves()) {
//            ResolvableType
//        }
        return getValves().stream().map(target -> (Valve<req, res>) target).collect(Collectors.toList());
    }

    public <req, res> List<Valve<req, res>> getTargetValves1(String groupName, Class<req> reqClazz, Class<res> resClazz) {
//        for (Valve<?, ?> valve : getValves()) {
//            ResolvableType
//        }

        List<Valve> targetValves = VALVE_MAP.get(groupName);
        if (CollectionUtils.isNotEmpty(targetValves)) {
            return targetValves.stream().map(target -> (Valve<req, res>) target).collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

//        for (Valve<?, ?> valve : valves) {
//            AnnotationMetadata annotationMetadata = AnnotationMetadata.introspect(valve.getClass());
//            Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(PipelineComponent.class.getName(), false);
//            System.out.println(annotationAttributes.get("groupName"));
//        }

//        ResolvableType resolvableType = ResolvableType.forClass(Valve.class);
//        String[] beanNamesForType = this.applicationContext.getBeanNamesForType(resolvableType);
//        Object bean = this.applicationContext.getBean(beanNamesForType[0]);

        Map<String, Valve> valveBeans = this.applicationContext.getBeansOfType(Valve.class);
//        Map<String, Object> valveBeans = this.applicationContext.getBeansWithAnnotation(PipelineComponent.class);
        // group by group name specified by the name() method of @RuleGroup annotation
        Map<String, List<Valve>> ruleBeanGroups = valveBeans.values().stream().collect(
                Collectors.groupingBy(
                        ruleBean -> Objects.requireNonNull(AnnotationUtils.findAnnotation(ruleBean.getClass(), ValveComponent.class)).groupName()
                )
        );
        VALVE_MAP.putAll(ruleBeanGroups);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static void main(String[] args) {
        System.out.println("----------------CollectionToCollectionConverter---------------");
        DefaultConversionService defaultConversionService = new DefaultConversionService();

        List<String> sourceList = Arrays.asList("1", "2", "2", "3", "4");
        TypeDescriptor sourceTypeDesp = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(String.class));
        TypeDescriptor targetTypeDesp = TypeDescriptor.collection(Set.class, TypeDescriptor.valueOf(Integer.class));

        Set<String> convert = (Set<String>) defaultConversionService.convert(sourceList, sourceTypeDesp, targetTypeDesp);
        System.out.println(convert.getClass());
        System.out.println(convert);
    }
}
