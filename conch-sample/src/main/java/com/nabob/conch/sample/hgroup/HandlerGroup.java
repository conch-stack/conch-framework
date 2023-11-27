package com.nabob.conch.sample.hgroup;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Adam
 * @since 2023/11/24
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
@Component
public @interface HandlerGroup {

    /**
     * group name
     */
    String groupName() default "";

    int groupName2() default 0;
}
