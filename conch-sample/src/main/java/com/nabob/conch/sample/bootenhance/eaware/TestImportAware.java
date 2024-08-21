package com.nabob.conch.sample.bootenhance.eaware;

import com.nabob.conch.sample.User;
import com.nabob.conch.sample.advice.EnableRpcLogV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

/**
 * TestImportAware
 * <p>
 * 知识点：如果想识别 ImportAware ，那么TestImportAware必须在 ImportRegistry 中存在，不然不会生效
 * <p>
 * 可使用 @Import(TestImportAware) 进行设置
 * 替代方案：
 *      ImportAware 其实就是为了获取 AnnotationMetadata 进而拿到自己想要的 注解什么的
 *      可以使用  ImportBeanDefinitionRegistrar 代替
 *
 * ImportAware是如何获取到注解元信息的呢？
 * 主要就是在ConfigurationClassPostProcessor中注入了一个ImportAwareBeanPostProcessor，在Bean的生命周期中将属性设置进去
 *
 * @author Adam
 * @since 2023/3/15
 */
//@Configuration  不生效
public class TestImportAware implements ImportAware {

    @Nullable
    protected AnnotationAttributes enableRpcLogV2;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        System.out.println("TestImportAware run setImportMetadata");

        this.enableRpcLogV2 = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableTestImportAware.class.getName(), false));
        if (this.enableRpcLogV2 == null) {
            throw new IllegalArgumentException(
                    "@EnableAsync is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean(name = "user1")
    public User user() {
        return new User("TestImportAware", 19);
    }
}
