package com.nabob.conch.sample.advice.agentv2;

import com.nabob.conch.sample.advice.EnableRpcLogV2;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 这个类非常重要，如果少了这个类，那么 ImportAware 将无法提前运行，这得益于继承关系，加载 @Configuration 的顺序
 *
 * @author Adam
 * @since 2023/3/15
 */
@Configuration
public abstract class AbstractRpcConfigurationConfiguration implements ImportAware {

    @Nullable
    protected AnnotationAttributes enableRpcLogV2;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableRpcLogV2 = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableRpcLogV2.class.getName(), false));
        if (this.enableRpcLogV2 == null) {
            throw new IllegalArgumentException(
                    "@EnableAsync is not present on importing class " + importMetadata.getClassName());
        }
    }
}
