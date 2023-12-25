package com.nabob.conch.sample.hgroup;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Adam
 * @since 2023/12/25
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface HandlerGroupV3 {

    /**
     * group name
     */
    String groupName() default "";

    int groupName2() default Integer.MIN_VALUE;

    String[] strForList() default {};

    int[] intForList() default {};
}
