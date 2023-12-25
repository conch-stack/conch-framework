package com.nabob.conch.sample.hgroup;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
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
public class HandlerGroupRegistryV2 implements ApplicationContextAware {


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
                HandlerGroupV2 annotation = AnnotationUtils.findAnnotation(handler.getClass(), HandlerGroupV2.class);
                if (Objects.nonNull(annotation)) {
                    if (null != annotation.strForList() && annotation.strForList().length > 0) {
                        for (String s : annotation.strForList()) {
                            result.put(s, handler);
                        }
                    } else if (null != annotation.intForList() && annotation.intForList().length > 0) {
                        for (int i : annotation.intForList()) {
                            result.put(String.valueOf(i), handler);
                        }
                    }
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
