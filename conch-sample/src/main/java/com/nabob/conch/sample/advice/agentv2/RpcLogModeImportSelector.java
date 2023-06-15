package com.nabob.conch.sample.advice.agentv2;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * 选择 RpcLog 模式
 *
 * @author Adam
 * @since 2023/3/15
 */
public abstract class RpcLogModeImportSelector<A extends Annotation> implements ImportSelector {

    /**
     * The default Rpc Log mode attribute name.
     */
    public static final String DEFAULT_RPC_LOG_MODE_ATTRIBUTE_NAME = "mode";

    /**
     * The name of the {@link RpcLogMode} attribute for the annotation specified by the
     * generic type {@code A}. The default is {@value #DEFAULT_RPC_LOG_MODE_ATTRIBUTE_NAME},
     * but subclasses may override in order to customize.
     */
    protected String getRpcLogModeAttributeName() {
        return DEFAULT_RPC_LOG_MODE_ATTRIBUTE_NAME;
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {

        // 获取 A 注解 的 Class 类型
        Class<?> annType = GenericTypeResolver.resolveTypeArgument(getClass(), RpcLogModeImportSelector.class);
        Assert.state(annType != null, "Unresolvable type argument for RpcLogModeImportSelector");

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(annType.getName(), false));
        if (attributes == null) {
            throw new IllegalArgumentException(String.format(
                    "@%s is not present on importing class '%s' as expected",
                    annType.getSimpleName(), importingClassMetadata.getClassName()));
        }

        RpcLogMode rpcLogMode = attributes.getEnum(getRpcLogModeAttributeName());
        String[] imports = selectImports(rpcLogMode);
        if (imports == null) {
            throw new IllegalArgumentException("Unknown RpcLogMode: " + rpcLogMode);
        }
        return imports;
    }

    @Nullable
    protected abstract String[] selectImports(RpcLogMode rpcLogMode);
}
