package com.nabob.conch.sample.bootenhance.importselect;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(CacheConfigurationSelector.class)
public @interface EnableImportSelectTest {

    /**
     * 使用 Client进行缓存
     * <p>
     * 默认使用：CacheMode DAO_SERVICE
     *
     * @return true use custom client to enable cache
     */
    CacheMode mode() default CacheMode.DAO_SERVICE;
}
