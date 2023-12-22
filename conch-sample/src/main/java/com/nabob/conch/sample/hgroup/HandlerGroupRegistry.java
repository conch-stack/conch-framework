package com.nabob.conch.sample.hgroup;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Valve Registry
 *
 * @author Adam
 * @since 2023/9/21
 */
@Component
public class HandlerGroupRegistry implements ApplicationContextAware {


    // key=handlerClass; value=Map<groupName, handlerInstance>
    private static final Map<Class<?>, Map<String, ?>> CACHE = Maps.newConcurrentMap();
    private static final Map<String, ?> DEFAULT_NULL = Maps.newHashMap();

    /**
     * Known spring application context object
     */
    private ApplicationContext applicationContext;

    public <Handler> Optional<Handler> get(String name, Class<Handler> handlerClass) {
        Optional<Map<String, Handler>> string = getString(handlerClass);
        if (string.isPresent()) {
            Map<String, Handler> stringHandlerMap = string.get();
            return Optional.ofNullable(stringHandlerMap.get(name));
        }
        return Optional.empty();
    }

    public <Handler> Optional<Handler> get(int name, Class<Handler> handlerClass) {
        Optional<Map<String, Handler>> string = getInt(handlerClass);
        if (string.isPresent()) {
            Map<String, Handler> stringHandlerMap = string.get();
            return Optional.ofNullable(stringHandlerMap.get(String.valueOf(name)));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <Handler> Optional<Map<String, Handler>> getString(Class<Handler> handlerClass) {
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
            Map<String, Handler> result = valveBeans.values().stream().filter(target -> target.getClass().isAnnotationPresent(HandlerGroup.class))
                    .collect(Collectors.toMap(target -> Objects.requireNonNull(AnnotationUtils.findAnnotation(target.getClass(), HandlerGroup.class)).groupName(), Function.identity(), (a, b) -> a));
            if (MapUtils.isNotEmpty(result)) {
                CACHE.put(handlerClass, result);
                return Optional.of(result);
            }
        }

        CACHE.put(handlerClass, DEFAULT_NULL);
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <Handler> Optional<Map<String, Handler>> getInt(Class<Handler> handlerClass) {
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
            Map<String, Handler> result = valveBeans.values().stream().filter(target -> target.getClass().isAnnotationPresent(HandlerGroup.class))
                    .collect(Collectors.toMap(target -> String.valueOf(Objects.requireNonNull(AnnotationUtils.findAnnotation(target.getClass(), HandlerGroup.class)).groupName2()), Function.identity(), (a, b) -> a));
            if (MapUtils.isNotEmpty(result)) {
                CACHE.put(handlerClass, result);
                return Optional.of(result);
            }
        }

        CACHE.put(handlerClass, DEFAULT_NULL);
        return Optional.empty();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
