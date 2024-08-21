package com.nabob.conch.sample.bootenhance.importselect;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * CacheModeImportSelector
 *
 * @author Adam
 * @since 2024/8/12
 */
public abstract class CacheModeImportSelector<A extends Annotation> implements ImportSelector {

    /**
     * The default cache mode attribute name.
     */
    public static final String DEFAULT_CACHE_MODE_ATTRIBUTE_NAME = "mode";

    @Override
    public final String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Class<?> annType = GenericTypeResolver.resolveTypeArgument(getClass(), CacheModeImportSelector.class);
        Assert.state(annType != null, "Unresolvable type argument for CacheModeImportSelector");

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(annType.getName(), false));
        if (attributes == null) {
            throw new IllegalArgumentException(String.format(
                "@%s is not present on importing class '%s' as expected",
                annType.getSimpleName(), importingClassMetadata.getClassName()));
        }

        CacheMode cacheMode = attributes.getEnum(DEFAULT_CACHE_MODE_ATTRIBUTE_NAME);
        String[] imports = selectImports(cacheMode);
        if (imports == null) {
            throw new IllegalArgumentException("Unknown CacheMode: " + cacheMode);
        }
        return imports;
    }

    protected abstract String[] selectImports(CacheMode cacheMode);

}
