package com.nabob.conch.spring.dependency.injection.selfannotation;

import java.lang.annotation.*;

/**
 * @author Adam
 * @date 2020/4/21
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectUser {
}
