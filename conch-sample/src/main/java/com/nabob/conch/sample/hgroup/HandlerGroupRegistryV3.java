package com.nabob.conch.sample.hgroup;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Adam
 * @since 2023/12/25
 */
@Component
public class HandlerGroupRegistryV3 implements ApplicationContextAware {
    // key=handlerClass; value=Map<groupName, handlerInstance>
    private static final Map<Class<?>, Map<String, ?>> CACHE = Maps.newConcurrentMap();
    private static final Map<String, ?> DEFAULT_NULL = Maps.newHashMap();

    /**
     * Known spring application context object
     */
    private ApplicationContext applicationContext;

    public <Handler> Optional<Handler> get(String name, Class<Handler> handlerClass) {
        Optional<Map<String, Handler>> string = doGet(handlerClass);
        if (string.isPresent()) {
            Map<String, Handler> stringHandlerMap = string.get();
            return Optional.ofNullable(stringHandlerMap.get(name));
        }
        return Optional.empty();
    }

    public <Handler> Optional<Handler> get(int name, Class<Handler> handlerClass) {
        Optional<Map<String, Handler>> string = doGet(handlerClass);
        if (string.isPresent()) {
            Map<String, Handler> stringHandlerMap = string.get();
            return Optional.ofNullable(stringHandlerMap.get(String.valueOf(name)));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <Handler> Optional<Map<String, Handler>> doGet(Class<Handler> handlerClass) {
        if (CACHE.containsKey(handlerClass)) {
            System.out.println("read from cache");
            Map<String, Handler> result = (Map<String, Handler>) CACHE.get(handlerClass);
            if (MapUtils.isEmpty(result) || Objects.equals(result, DEFAULT_NULL)) {
                return Optional.empty();
            }
            return Optional.of(result);
        }

        Map<String, Handler> valveBeans = this.applicationContext.getBeansOfType(handlerClass);
        if (MapUtils.isNotEmpty(valveBeans)) {
            Map<String, Handler> result = Maps.newHashMap();
            for (Handler handler : valveBeans.values()) {
                Set<String> handlerForSet = getHandlerForKeySet(AnnotationUtils.findAnnotation(handler.getClass(), HandlerGroupV3.class));
                if (CollectionUtils.isNotEmpty(handlerForSet)) {
                    handlerForSet.forEach(target -> {
                        if (result.containsKey(target)) {
                            System.err.printf("Handler %s for key %s has already existed, please check out your code%n", handler.getClass(), target);
                        } else {
                            result.put(target, handler);
                        }
                    });
                }
            }

            if (MapUtils.isNotEmpty(result)) {
                CACHE.put(handlerClass, result);
                return Optional.of(result);
            }
        }

        CACHE.put(handlerClass, DEFAULT_NULL);
        return Optional.empty();
    }

    private Set<String> getHandlerForKeySet(HandlerGroupV3 annotation) {
        Set<String> result = Sets.newHashSet();
        if (Objects.nonNull(annotation)) {
            if (StringUtils.isNotBlank(annotation.groupName())) {
                result.add(annotation.groupName());
            }
            if (annotation.groupName2() != Integer.MIN_VALUE) {
                result.add(formatInt(annotation.groupName2()));
            }
            if (null != annotation.strForList() && annotation.strForList().length > 0) {
                result.addAll(Arrays.asList(annotation.strForList()));
            }
            if (null != annotation.intForList() && annotation.intForList().length > 0) {
                for (int i : annotation.intForList()) {
                    result.add(formatInt(i));
                }
            }
        }
        return result;
    }

    private static String formatInt(int intFor) {
        return String.format("$int_%d", intFor);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
