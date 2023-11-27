package com.nabob.conch.sample.hgroup;

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
        Optional<Map<Integer, Handler>> string = getInt(handlerClass);
        if (string.isPresent()) {
            Map<Integer, Handler> stringHandlerMap = string.get();
            return Optional.ofNullable(stringHandlerMap.get(name));
        }
        return Optional.empty();
    }

    public <Handler> Optional<Map<String, Handler>> getString(Class<Handler> handlerClass) {
        Map<String, Handler> valveBeans = this.applicationContext.getBeansOfType(handlerClass);

        if (MapUtils.isNotEmpty(valveBeans)) {
            return Optional.of(valveBeans.values().stream().filter(target -> target.getClass().isAnnotationPresent(HandlerGroup.class))
                    .collect(Collectors.toMap(target -> Objects.requireNonNull(AnnotationUtils.findAnnotation(target.getClass(), HandlerGroup.class)).groupName(), Function.identity(), (a, b) -> a)));
        }

        return Optional.empty();
    }

    public <Handler> Optional<Map<Integer, Handler>> getInt(Class<Handler> handlerClass) {
        Map<String, Handler> valveBeans = this.applicationContext.getBeansOfType(handlerClass);

        if (MapUtils.isNotEmpty(valveBeans)) {
            return Optional.of(valveBeans.values().stream().filter(target -> target.getClass().isAnnotationPresent(HandlerGroup.class))
                    .collect(Collectors.toMap(target -> Objects.requireNonNull(AnnotationUtils.findAnnotation(target.getClass(), HandlerGroup.class)).groupName2(), Function.identity(), (a, b) -> a)));
        }

        return Optional.empty();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
