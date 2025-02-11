package com.nabob.conch.sample.reflect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FirstAnnotation {

    String value() default "";
}