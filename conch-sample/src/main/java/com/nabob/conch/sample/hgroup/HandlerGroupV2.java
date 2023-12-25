package com.nabob.conch.sample.hgroup;

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
@Component
public @interface HandlerGroupV2 {

    String[] strForList() default {};

    int[] intForList() default {};
}
