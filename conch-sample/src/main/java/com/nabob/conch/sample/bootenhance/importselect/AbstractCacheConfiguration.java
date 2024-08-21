package com.nabob.conch.sample.bootenhance.importselect;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * AbstractCacheConfiguration
 *
 * @author Adam
 * @since 2024/8/12
 */
@Configuration
public abstract class AbstractCacheConfiguration implements ImportAware {

    protected AnnotationAttributes enableCacheAnnotation;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableCacheAnnotation = AnnotationAttributes.fromMap(
            importMetadata.getAnnotationAttributes(EnableImportSelectTest.class.getName(), false));
        if (this.enableCacheAnnotation == null) {
            throw new IllegalArgumentException(
                "@EnableImportSelectTest is not present on importing class " + importMetadata.getClassName());
        }
    }
}
