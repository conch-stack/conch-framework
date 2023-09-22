package com.nabob.conch.sample.pipeline;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Pipeline Component
 *
 * @author Adam
 * @date 2023/9/18
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
@Component
public @interface ValveComponent {

    /**
     * group name
     */
    String groupName() default "default";
}